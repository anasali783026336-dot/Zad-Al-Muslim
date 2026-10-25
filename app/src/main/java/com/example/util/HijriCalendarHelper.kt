package com.example.util

import com.example.model.HijriDate
import com.example.model.IslamicEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object HijriCalendarHelper {

    val hijriMonthNames = listOf(
        "المُحَرَّم",
        "صَفَر",
        "رَبِيع الأَوَّل",
        "رَبِيع الآخِر",
        "جُمَادَى الأُولَى",
        "جُمَادَى الآخِرَة",
        "رَجَب",
        "شَعْبَان",
        "رَمَضَان",
        "شَوَّال",
        "ذُو القَعْدَة",
        "ذُو الحِجَّة"
    )

    val islamicEvents = listOf(
        IslamicEvent(1, 1, "رأس السنة الهجرية", "بداية العام الهجري الجديد وهجرة النبي المصطفى صلى الله عليه وسلم."),
        IslamicEvent(10, 1, "يوم عاشوراء", "اليوم العاشر من محرم، صامه النبي صلى الله عليه وسلم شكراً لله على نجاة موسى عليه السلام."),
        IslamicEvent(12, 3, "المولد النبوي الشريف", "ذكرى مولد سيد الخلق وخاتم الأنبياء محمد صلى الله عليه وسلم."),
        IslamicEvent(27, 7, "الإسراء والمعراج", "ذكرى معجزة الإسراء من المسجد الحرام إلى المسجد الأقصى والمعراج إلى سدرة المنتهى."),
        IslamicEvent(15, 8, "ليلة النصف من شعبان", "ليلة مباركة يستحب فيها قيام الليل والدعاء والاستغفار."),
        IslamicEvent(1, 9, "غرة شهر رمضان المبارك", "أول أيام شهر الصيام والقرآن والرحمة والمغفرة."),
        IslamicEvent(17, 9, "غزوة بدر الكبرى", "يوم الفرقان يوم التقى الجمعان في السابع عشر من شهر رمضان."),
        IslamicEvent(27, 9, "ليلة القدر المباركة", "خير من ألف شهر، تنزل فيها الملائكة والروح."),
        IslamicEvent(1, 10, "عيد الفطر المبارك", "يوم الجائزة والفرح بإتمام صيام شهر رمضان المبارك."),
        IslamicEvent(9, 12, "يوم عرفة", "أعظم أيام العام وصيامه يكفر ذنوب سنتين، سنة ماضية وسنة باقية."),
        IslamicEvent(10, 12, "عيد الأضحى المبارك", "يوم النحر وأعظم أيام الحج وإحياء سنة الخليل إبراهيم عليه السلام."),
        IslamicEvent(11, 12, "أيام التشريق", "أيام أكل وشرب وذكر لله تعالى عقب يوم النحر.")
    )

    /**
     * Converts a Gregorian Calendar date to algorithmic Hijri Date
     * with optional user day offset adjustment.
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance(), dayOffset: Int = 0): HijriDate {
        val cal = calendar.clone() as Calendar
        if (dayOffset != 0) {
            cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Julian Day Number calculation
        var a = floor((14 - month) / 12.0)
        var y = year + 4800 - a
        var m = month + 12 * a - 3
        var jd = day + floor((153 * m + 2) / 5.0) + 365 * y + floor(y / 4.0) - floor(y / 100.0) + floor(y / 400.0) - 32045

        // Islamic conversion from Julian Day
        val l = jd - 1948440 + 10632
        val n = floor((l - 1) / 10631.0)
        val l2 = l - 10631 * n + 354
        val j = (floor((10985 - l2) / 5316.0)) * (floor((50 * l2) / 17719.0)) + (floor(l2 / 5670.0)) * (floor((43 * l2) / 15238.0))
        val l3 = l2 - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) - (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
        val hMonth = floor((24 * l3) / 709.0).toInt()
        val hDay = (l3 - floor((709 * hMonth) / 24.0)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        val normalizedMonth = when {
            hMonth < 1 -> 1
            hMonth > 12 -> 12
            else -> hMonth
        }

        val monthName = hijriMonthNames.getOrElse(normalizedMonth - 1) { "محرم" }
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("ar"))
        val dayOfWeekName = dayOfWeekFormat.format(cal.time)

        val gregorianFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
        val gregorianFormatted = gregorianFormat.format(cal.time)

        return HijriDate(
            day = maxOf(1, hDay),
            month = normalizedMonth,
            year = hYear,
            monthName = monthName,
            dayOfWeekName = dayOfWeekName,
            gregorianFormatted = gregorianFormatted
        )
    }

    fun getEventsForMonth(hijriMonth: Int): List<IslamicEvent> {
        return islamicEvents.filter { it.hijriMonth == hijriMonth }
    }

    fun getEventForDate(hijriDay: Int, hijriMonth: Int): IslamicEvent? {
        return islamicEvents.find { it.hijriDay == hijriDay && it.hijriMonth == hijriMonth }
    }
}
