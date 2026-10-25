package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("zad_preferences", Context.MODE_PRIVATE)

    // Tasbih
    var tasbihCount: Int
        get() = prefs.getInt("tasbih_count", 0)
        set(value) = prefs.edit().putInt("tasbih_count", value).apply()

    var tasbihLifetimeTotal: Int
        get() = prefs.getInt("tasbih_lifetime_total", 0)
        set(value) = prefs.edit().putInt("tasbih_lifetime_total", value).apply()

    var tasbihTarget: Int
        get() = prefs.getInt("tasbih_target", 33)
        set(value) = prefs.edit().putInt("tasbih_target", value).apply()

    var tasbihZikr: String
        get() = prefs.getString("tasbih_zikr", "سُبْحَانَ اللَّهِ") ?: "سُبْحَانَ اللَّهِ"
        set(value) = prefs.edit().putString("tasbih_zikr", value).apply()

    var tasbihVibration: Boolean
        get() = prefs.getBoolean("tasbih_vibration", true)
        set(value) = prefs.edit().putBoolean("tasbih_vibration", value).apply()

    // Hijri Adjustment
    var hijriDayOffset: Int
        get() = prefs.getInt("hijri_day_offset", 0)
        set(value) = prefs.edit().putInt("hijri_day_offset", value).apply()

    // Quran Font Size Scale
    var quranFontSize: Float
        get() = prefs.getFloat("quran_font_size", 22f)
        set(value) = prefs.edit().putFloat("quran_font_size", value).apply()

    // Notifications
    var morningAzkarEnabled: Boolean
        get() = prefs.getBoolean("morning_azkar_enabled", true)
        set(value) = prefs.edit().putBoolean("morning_azkar_enabled", value).apply()

    var morningAzkarHour: Int
        get() = prefs.getInt("morning_azkar_hour", 6)
        set(value) = prefs.edit().putInt("morning_azkar_hour", value).apply()

    var morningAzkarMinute: Int
        get() = prefs.getInt("morning_azkar_minute", 30)
        set(value) = prefs.edit().putInt("morning_azkar_minute", value).apply()

    var eveningAzkarEnabled: Boolean
        get() = prefs.getBoolean("evening_azkar_enabled", true)
        set(value) = prefs.edit().putBoolean("evening_azkar_enabled", value).apply()

    var eveningAzkarHour: Int
        get() = prefs.getInt("evening_azkar_hour", 17)
        set(value) = prefs.edit().putInt("evening_azkar_hour", value).apply()

    var eveningAzkarMinute: Int
        get() = prefs.getInt("evening_azkar_minute", 30)
        set(value) = prefs.edit().putInt("evening_azkar_minute", value).apply()
}
