package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.data.QuranDataSource
import com.example.data.RecitersDataSource
import com.example.model.Reciter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurahId: Int = 1,
    val currentSurahName: String = "الفاتحة",
    val currentReciter: Reciter = RecitersDataSource.reciters.first(),
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val errorMessage: String? = null,
    val isOfflineFile: Boolean = false
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    _state.value = _state.value.copy(
                        currentPositionMs = mp.currentPosition,
                        durationMs = mp.duration
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun playSurah(surahId: Int, reciter: Reciter) {
        val surah = QuranDataSource.getSurahById(surahId) ?: return

        stop()

        _state.value = _state.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentSurahId = surahId,
            currentSurahName = surah.nameArabic,
            currentReciter = reciter,
            currentPositionMs = 0,
            durationMs = 0,
            errorMessage = null
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                // Check if downloaded offline file exists
                val offlineFile = getDownloadedFile(surahId, reciter.id)
                val isOffline = offlineFile.exists() && offlineFile.length() > 0

                if (isOffline) {
                    setDataSource(offlineFile.absolutePath)
                    _state.value = _state.value.copy(isOfflineFile = true)
                } else {
                    val streamUrl = RecitersDataSource.getAudioUrl(reciter, surahId)
                    setDataSource(streamUrl)
                    _state.value = _state.value.copy(isOfflineFile = false)
                }

                setOnPreparedListener { mp ->
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    mp.start()
                    handler.post(progressRunnable)
                }

                setOnCompletionListener {
                    _state.value = _state.value.copy(isPlaying = false, currentPositionMs = 0)
                    handler.removeCallbacks(progressRunnable)
                    // Auto play next surah if available
                    if (surahId < 114) {
                        playSurah(surahId + 1, reciter)
                    }
                }

                setOnErrorListener { _, what, extra ->
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "تعذر تشغيل التلاوة. يرجى التحقق من اتصال الإنترنت أو محاولة إعادة التشغيل."
                    )
                    handler.removeCallbacks(progressRunnable)
                    true
                }

                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "خطأ في تهيئة المشغل: ${e.localizedMessage ?: "تأكد من الاتصال بالإنترنت"}"
            )
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _state.value = _state.value.copy(isPlaying = false)
                handler.removeCallbacks(progressRunnable)
            } else {
                mp.start()
                _state.value = _state.value.copy(isPlaying = true)
                handler.post(progressRunnable)
            }
        } ?: run {
            playSurah(_state.value.currentSurahId, _state.value.currentReciter)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _state.value = _state.value.copy(currentPositionMs = positionMs)
    }

    fun playNext() {
        val nextSurah = if (_state.value.currentSurahId < 114) _state.value.currentSurahId + 1 else 1
        playSurah(nextSurah, _state.value.currentReciter)
    }

    fun playPrevious() {
        val prevSurah = if (_state.value.currentSurahId > 1) _state.value.currentSurahId - 1 else 114
        playSurah(prevSurah, _state.value.currentReciter)
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
            it.release()
        }
        mediaPlayer = null
        _state.value = _state.value.copy(isPlaying = false, isBuffering = false)
    }

    fun release() {
        stop()
    }

    private fun getDownloadedFile(surahId: Int, reciterId: String): File {
        val dir = File(context.filesDir, "audio/$reciterId")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "${surahId}.mp3")
    }
}
