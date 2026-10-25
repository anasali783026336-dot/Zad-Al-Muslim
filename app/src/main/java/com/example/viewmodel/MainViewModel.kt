package com.example.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AzkarDataSource
import com.example.data.QuranDataSource
import com.example.data.RecitersDataSource
import com.example.data.ZadRepository
import com.example.model.Ayah
import com.example.model.Bookmark
import com.example.model.HijriDate
import com.example.model.IslamicEvent
import com.example.model.Reciter
import com.example.model.Surah
import com.example.model.ZikrItem
import com.example.player.AudioPlayerManager
import com.example.player.AudioPlayerState
import com.example.util.HijriCalendarHelper
import com.example.util.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class Screen {
    object Home : Screen()
    object QuranIndex : Screen()
    data class QuranReader(val surahId: Int, val highlightAyah: Int = 1) : Screen()
    data class Azkar(val initialCategory: String = "الصباح") : Screen()
    object Tasbih : Screen()
    object CalendarView : Screen()
    object AudioPlayer : Screen()
    object NotificationSettings : Screen()
}

class MainViewModel(
    private val context: Context,
    val repository: ZadRepository,
    val playerManager: AudioPlayerManager
) : ViewModel() {

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    fun navigateHome() {
        _screenStack.value = listOf(Screen.Home)
    }

    // Bookmarks & Favorites
    val latestBookmark: StateFlow<Bookmark?> = repository.latestBookmark.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteIds: StateFlow<List<Int>> = repository.favoriteZikrIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Quran State
    private val _quranSearchQuery = MutableStateFlow("")
    val quranSearchQuery: StateFlow<String> = _quranSearchQuery.asStateFlow()

    private val _quranFilter = MutableStateFlow("الكل") // الكل, مكية, مدنية
    val quranFilter: StateFlow<String> = _quranFilter.asStateFlow()

    val surahs: List<Surah> = QuranDataSource.surahs

    private val _quranFontSize = MutableStateFlow(repository.getQuranFontSize())
    val quranFontSize: StateFlow<Float> = _quranFontSize.asStateFlow()

    fun setQuranSearch(query: String) {
        _quranSearchQuery.value = query
    }

    fun setQuranFilter(filter: String) {
        _quranFilter.value = filter
    }

    fun setQuranFontSize(size: Float) {
        _quranFontSize.value = size
        repository.setQuranFontSize(size)
    }

    fun saveBookmark(surahId: Int, ayahNumber: Int) {
        viewModelScope.launch {
            val surah = QuranDataSource.getSurahById(surahId)
            repository.saveBookmark(
                surahId = surahId,
                surahName = surah?.nameArabic ?: "سورة $surahId",
                ayahNumber = ayahNumber
            )
        }
    }

    fun getVerses(surahId: Int): List<Ayah> {
        return QuranDataSource.getVersesForSurah(surahId)
    }

    // Azkar State
    private val _azkarCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val azkarCounts: StateFlow<Map<Int, Int>> = _azkarCounts.asStateFlow()

    private val _azkarSearchQuery = MutableStateFlow("")
    val azkarSearchQuery: StateFlow<String> = _azkarSearchQuery.asStateFlow()

    fun setAzkarSearch(query: String) {
        _azkarSearchQuery.value = query
    }

    fun incrementZikrCount(item: ZikrItem) {
        val current = _azkarCounts.value[item.id] ?: 0
        if (current < item.targetCount) {
            val next = current + 1
            _azkarCounts.value = _azkarCounts.value + (item.id to next)
            vibrate(if (next >= item.targetCount) 80 else 25)
        }
    }

    fun resetZikrCount(itemId: Int) {
        _azkarCounts.value = _azkarCounts.value + (itemId to 0)
    }

    fun toggleFavorite(zikrId: Int) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(zikrId)
            repository.toggleFavorite(zikrId, isFav)
        }
    }

    // Tasbih State
    private val _tasbihCount = MutableStateFlow(repository.getTasbihCount())
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(repository.getTasbihTarget())
    val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

    private val _tasbihLifetime = MutableStateFlow(repository.getTasbihLifetimeTotal())
    val tasbihLifetime: StateFlow<Int> = _tasbihLifetime.asStateFlow()

    private val _tasbihZikr = MutableStateFlow(repository.getTasbihZikr())
    val tasbihZikr: StateFlow<String> = _tasbihZikr.asStateFlow()

    private val _tasbihVibration = MutableStateFlow(repository.getTasbihVibration())
    val tasbihVibration: StateFlow<Boolean> = _tasbihVibration.asStateFlow()

    fun incrementTasbih() {
        val next = _tasbihCount.value + 1
        _tasbihCount.value = next
        repository.setTasbihCount(next)

        val nextLifetime = _tasbihLifetime.value + 1
        _tasbihLifetime.value = nextLifetime
        repository.incrementTasbihLifetime()

        if (_tasbihVibration.value) {
            val isCycleComplete = _tasbihTarget.value > 0 && next % _tasbihTarget.value == 0
            vibrate(if (isCycleComplete) 90 else 30)
        }
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
        repository.setTasbihCount(0)
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
        repository.setTasbihTarget(target)
    }

    fun setTasbihZikr(zikr: String) {
        _tasbihZikr.value = zikr
        repository.setTasbihZikr(zikr)
    }

    fun toggleTasbihVibration() {
        val next = !_tasbihVibration.value
        _tasbihVibration.value = next
        repository.setTasbihVibration(next)
    }

    // Hijri Calendar State
    private val _hijriOffset = MutableStateFlow(repository.getHijriOffset())
    val hijriOffset: StateFlow<Int> = _hijriOffset.asStateFlow()

    private val _calendarMonthOffset = MutableStateFlow(0) // relative to current month
    val calendarMonthOffset: StateFlow<Int> = _calendarMonthOffset.asStateFlow()

    fun adjustHijriOffset(delta: Int) {
        val next = (_hijriOffset.value + delta).coerceIn(-3, 3)
        _hijriOffset.value = next
        repository.setHijriOffset(next)
    }

    fun nextCalendarMonth() {
        _calendarMonthOffset.value = _calendarMonthOffset.value + 1
    }

    fun previousCalendarMonth() {
        _calendarMonthOffset.value = _calendarMonthOffset.value - 1
    }

    fun resetCalendarMonth() {
        _calendarMonthOffset.value = 0
    }

    fun getTodayHijriDate(): HijriDate {
        return HijriCalendarHelper.getHijriDate(Calendar.getInstance(), _hijriOffset.value)
    }

    fun getDisplayedHijriDate(): HijriDate {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, _calendarMonthOffset.value)
        return HijriCalendarHelper.getHijriDate(cal, _hijriOffset.value)
    }

    fun getEventsForCurrentDisplayedMonth(): List<IslamicEvent> {
        val displayed = getDisplayedHijriDate()
        return HijriCalendarHelper.getEventsForMonth(displayed.month)
    }

    // Audio Reciter & Player State
    val playerState: StateFlow<AudioPlayerState> = playerManager.state
    val downloadStatus = repository.downloadManager.downloadStatus
    val downloadedSurahs = repository.downloadedSurahs

    fun playSurah(surahId: Int, reciter: Reciter = playerState.value.currentReciter) {
        playerManager.playSurah(surahId, reciter)
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun seekAudio(positionMs: Int) {
        playerManager.seekTo(positionMs)
    }

    fun playNextSurah() {
        playerManager.playNext()
    }

    fun playPreviousSurah() {
        playerManager.playPrevious()
    }

    fun downloadSurah(surahId: Int, reciter: Reciter) {
        viewModelScope.launch {
            val surah = QuranDataSource.getSurahById(surahId)
            repository.downloadSurah(surahId, surah?.nameArabic ?: "سورة $surahId", reciter)
        }
    }

    fun deleteDownloadedSurah(surahId: Int, reciterId: String) {
        viewModelScope.launch {
            repository.deleteDownloadedSurah(surahId, reciterId)
        }
    }

    // Notifications Settings
    private val _morningEnabled = MutableStateFlow(repository.getMorningNotificationSettings().first)
    val morningEnabled: StateFlow<Boolean> = _morningEnabled.asStateFlow()

    private val _morningHour = MutableStateFlow(repository.getMorningNotificationSettings().second)
    val morningHour: StateFlow<Int> = _morningHour.asStateFlow()

    private val _morningMinute = MutableStateFlow(repository.getMorningNotificationSettings().third)
    val morningMinute: StateFlow<Int> = _morningMinute.asStateFlow()

    private val _eveningEnabled = MutableStateFlow(repository.getEveningNotificationSettings().first)
    val eveningEnabled: StateFlow<Boolean> = _eveningEnabled.asStateFlow()

    private val _eveningHour = MutableStateFlow(repository.getEveningNotificationSettings().second)
    val eveningHour: StateFlow<Int> = _eveningHour.asStateFlow()

    private val _eveningMinute = MutableStateFlow(repository.getEveningNotificationSettings().third)
    val eveningMinute: StateFlow<Int> = _eveningMinute.asStateFlow()

    fun updateMorningNotification(enabled: Boolean, hour: Int, minute: Int) {
        _morningEnabled.value = enabled
        _morningHour.value = hour
        _morningMinute.value = minute
        repository.setMorningNotification(enabled, hour, minute)
    }

    fun updateEveningNotification(enabled: Boolean, hour: Int, minute: Int) {
        _eveningEnabled.value = enabled
        _eveningHour.value = hour
        _eveningMinute.value = minute
        repository.setEveningNotification(enabled, hour, minute)
    }

    fun testNotification() {
        NotificationScheduler.triggerImmediateTestNotification(context)
    }

    private fun vibrate(durationMs: Long) {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }

    class Factory(
        private val context: Context,
        private val repository: ZadRepository,
        private val playerManager: AudioPlayerManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(context, repository, playerManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
