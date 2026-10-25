package com.example.data

import com.example.model.Reciter
import java.util.Locale

object RecitersDataSource {

    val reciters: List<Reciter> = listOf(
        Reciter(
            id = "yasser_al_dossari",
            nameArabic = "الشيخ ياسر الدوسري",
            serverBaseUrl = "https://server11.mp3quran.net/yasser/",
            description = "إمام وخطيب المسجد الحرام بمكة المكرمة - رواية حفص عن عاصم"
        ),
        Reciter(
            id = "mishary_alafasy",
            nameArabic = "الشيخ مشاري راشد العفاسي",
            serverBaseUrl = "https://server8.mp3quran.net/afs/",
            description = "تلاوة خاشعة مرتلة برواية حفص عن عاصم"
        ),
        Reciter(
            id = "abdulbasit_murattal",
            nameArabic = "الشيخ عبد الباسط عبد الصمد",
            serverBaseUrl = "https://server7.mp3quran.net/basit/",
            description = "المصحف المرتل برواية حفص عن عاصم"
        ),
        Reciter(
            id = "saad_al_ghamdi",
            nameArabic = "الشيخ سعد الغامدي",
            serverBaseUrl = "https://server7.mp3quran.net/s_gmd/",
            description = "تلاوة هادئة ومتقنة برواية حفص عن عاصم"
        ),
        Reciter(
            id = "maher_al_muaiqly",
            nameArabic = "الشيخ ماهر المعيقلي",
            serverBaseUrl = "https://server12.mp3quran.net/maher/",
            description = "إمام الحرم المكي الشريف"
        )
    )

    fun getAudioUrl(reciter: Reciter, surahId: Int): String {
        val paddedSurah = String.format(Locale.US, "%03d.mp3", surahId)
        return reciter.serverBaseUrl + paddedSurah
    }

    fun getReciterById(id: String): Reciter {
        return reciters.find { it.id == id } ?: reciters.first()
    }
}
