package com.example.player

import android.content.Context
import com.example.data.RecitersDataSource
import com.example.data.local.DownloadedSurahEntity
import com.example.data.local.ZadDao
import com.example.model.Reciter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class DownloadProgress(
    val surahId: Int,
    val reciterId: String,
    val isDownloading: Boolean = false,
    val progressPercent: Int = 0,
    val error: String? = null
)

class AudioDownloadManager(
    private val context: Context,
    private val zadDao: ZadDao
) {
    private val _downloadStatus = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadStatus: StateFlow<Map<String, DownloadProgress>> = _downloadStatus.asStateFlow()

    fun getFileKey(surahId: Int, reciterId: String): String = "${surahId}_$reciterId"

    suspend fun isSurahDownloaded(surahId: Int, reciterId: String): Boolean {
        val file = getDestinationFile(surahId, reciterId)
        return file.exists() && file.length() > 0
    }

    suspend fun downloadSurah(
        surahId: Int,
        surahName: String,
        reciter: Reciter
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val key = getFileKey(surahId, reciter.id)
        val file = getDestinationFile(surahId, reciter.id)

        try {
            _downloadStatus.value = _downloadStatus.value + (key to DownloadProgress(
                surahId = surahId,
                reciterId = reciter.id,
                isDownloading = true,
                progressPercent = 0
            ))

            val downloadUrl = RecitersDataSource.getAudioUrl(reciter, surahId)
            val connection = URL(downloadUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("تعذر الاتصال بالخادم: كود ${connection.responseCode}")
            }

            val fileLength = connection.contentLength
            val input = connection.inputStream
            val output = FileOutputStream(file)

            val buffer = ByteArray(8192)
            var total: Long = 0
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                total += count
                output.write(buffer, 0, count)
                if (fileLength > 0) {
                    val progress = ((total * 100) / fileLength).toInt()
                    _downloadStatus.value = _downloadStatus.value + (key to DownloadProgress(
                        surahId = surahId,
                        reciterId = reciter.id,
                        isDownloading = true,
                        progressPercent = progress
                    ))
                }
            }

            output.flush()
            output.close()
            input.close()

            // Save in database
            zadDao.insertDownloadedSurah(
                DownloadedSurahEntity(
                    surahId = surahId,
                    reciterId = reciter.id,
                    surahName = surahName,
                    reciterName = reciter.nameArabic,
                    localFilePath = file.absolutePath,
                    fileSizeBytes = file.length()
                )
            )

            _downloadStatus.value = _downloadStatus.value + (key to DownloadProgress(
                surahId = surahId,
                reciterId = reciter.id,
                isDownloading = false,
                progressPercent = 100
            ))

            Result.success(Unit)
        } catch (e: Exception) {
            if (file.exists()) file.delete()
            val msg = e.localizedMessage ?: "فشل التنزيل، تأكد من الاتصال بالإنترنت"
            _downloadStatus.value = _downloadStatus.value + (key to DownloadProgress(
                surahId = surahId,
                reciterId = reciter.id,
                isDownloading = false,
                progressPercent = 0,
                error = msg
            ))
            Result.failure(e)
        }
    }

    suspend fun deleteDownloadedSurah(surahId: Int, reciterId: String) = withContext(Dispatchers.IO) {
        val file = getDestinationFile(surahId, reciterId)
        if (file.exists()) {
            file.delete()
        }
        zadDao.deleteDownloadedSurah(surahId, reciterId)
        val key = getFileKey(surahId, reciterId)
        _downloadStatus.value = _downloadStatus.value - key
    }

    private fun getDestinationFile(surahId: Int, reciterId: String): File {
        val dir = File(context.filesDir, "audio/$reciterId")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$surahId.mp3")
    }
}
