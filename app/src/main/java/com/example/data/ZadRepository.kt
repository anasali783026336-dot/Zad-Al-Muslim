package com.example.data

import android.content.Context
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedSurahEntity
import com.example.data.local.FavoriteZikrEntity
import com.example.data.local.UserPreferences
import com.example.data.local.ZadDao
import com.example.model.Bookmark
import com.example.model.Reciter
import com.example.player.AudioDownloadManager
import com.example.util.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ZadRepository(
    private val context: Context,
    private val zadDao: ZadDao,
    private val prefs: UserPreferences,
    val downloadManager: AudioDownloadManager
) {
    // Bookmarks
    val latestBookmark: Flow<Bookmark?> = zadDao.getLatestBookmark().map { entity ->
        entity?.let {
            Bookmark(
                surahId = it.surahId,
                surahName = it.surahName,
                ayahNumber = it.ayahNumber,
                timestamp = it.timestamp
            )
        }
    }

    suspend fun saveBookmark(surahId: Int, surahName: String, ayahNumber: Int) {
        zadDao.saveBookmark(
            BookmarkEntity(
                id = 1,
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Favorite Azkar
    val favoriteZikrIds: Flow<List<Int>> = zadDao.getFavoriteZikrIds()

    suspend fun toggleFavorite(zikrId: Int, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            zadDao.removeFavoriteZikr(zikrId)
        } else {
            zadDao.addFavoriteZikr(FavoriteZikrEntity(zikrId = zikrId))
        }
    }

    // Downloaded Surahs
    val downloadedSurahs: Flow<List<DownloadedSurahEntity>> = zadDao.getAllDownloadedSurahs()

    suspend fun downloadSurah(surahId: Int, surahName: String, reciter: Reciter): Result<Unit> {
        return downloadManager.downloadSurah(surahId, surahName, reciter)
    }

    suspend fun deleteDownloadedSurah(surahId: Int, reciterId: String) {
        downloadManager.deleteDownloadedSurah(surahId, reciterId)
    }

    // Tasbih Persistence
    fun getTasbihCount(): Int = prefs.tasbihCount
    fun setTasbihCount(count: Int) {
        prefs.tasbihCount = count
    }

    fun getTasbihLifetimeTotal(): Int = prefs.tasbihLifetimeTotal
    fun incrementTasbihLifetime() {
        prefs.tasbihLifetimeTotal = prefs.tasbihLifetimeTotal + 1
    }

    fun getTasbihTarget(): Int = prefs.tasbihTarget
    fun setTasbihTarget(target: Int) {
        prefs.tasbihTarget = target
    }

    fun getTasbihZikr(): String = prefs.tasbihZikr
    fun setTasbihZikr(zikr: String) {
        prefs.tasbihZikr = zikr
    }

    fun getTasbihVibration(): Boolean = prefs.tasbihVibration
    fun setTasbihVibration(enabled: Boolean) {
        prefs.tasbihVibration = enabled
    }

    // Preferences & Settings
    fun getQuranFontSize(): Float = prefs.quranFontSize
    fun setQuranFontSize(size: Float) {
        prefs.quranFontSize = size
    }

    fun getHijriOffset(): Int = prefs.hijriDayOffset
    fun setHijriOffset(offset: Int) {
        prefs.hijriDayOffset = offset
    }

    fun getMorningNotificationSettings(): Triple<Boolean, Int, Int> {
        return Triple(prefs.morningAzkarEnabled, prefs.morningAzkarHour, prefs.morningAzkarMinute)
    }

    fun setMorningNotification(enabled: Boolean, hour: Int, minute: Int) {
        prefs.morningAzkarEnabled = enabled
        prefs.morningAzkarHour = hour
        prefs.morningAzkarMinute = minute
        if (enabled) {
            NotificationScheduler.scheduleMorningAlarm(context, hour, minute)
        } else {
            NotificationScheduler.cancelAlarm(
                context,
                NotificationScheduler.ACTION_MORNING_AZKAR,
                NotificationScheduler.REQUEST_CODE_MORNING
            )
        }
    }

    fun getEveningNotificationSettings(): Triple<Boolean, Int, Int> {
        return Triple(prefs.eveningAzkarEnabled, prefs.eveningAzkarHour, prefs.eveningAzkarMinute)
    }

    fun setEveningNotification(enabled: Boolean, hour: Int, minute: Int) {
        prefs.eveningAzkarEnabled = enabled
        prefs.eveningAzkarHour = hour
        prefs.eveningAzkarMinute = minute
        if (enabled) {
            NotificationScheduler.scheduleEveningAlarm(context, hour, minute)
        } else {
            NotificationScheduler.cancelAlarm(
                context,
                NotificationScheduler.ACTION_EVENING_AZKAR,
                NotificationScheduler.REQUEST_CODE_EVENING
            )
        }
    }
}
