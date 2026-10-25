package com.example.model

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: String, // مكية أو مدنية
    val pageNumber: Int,
    val juzNumber: Int
)

data class Ayah(
    val numberInSurah: Int,
    val textUthmani: String,
    val tafsir: String = "",
    val page: Int = 1,
    val juz: Int = 1
)

data class Bookmark(
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ZikrItem(
    val id: Int,
    val category: String, // الصباح, المساء, بعد الصلاة, النوم, أدعية
    val text: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val virtue: String = "",
    val source: String = "",
    var isFavorite: Boolean = false
)

data class Reciter(
    val id: String,
    val nameArabic: String,
    val serverBaseUrl: String,
    val description: String = ""
)

data class IslamicEvent(
    val hijriDay: Int,
    val hijriMonth: Int, // 1 to 12
    val title: String,
    val description: String
)

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthName: String,
    val dayOfWeekName: String,
    val gregorianFormatted: String
)
