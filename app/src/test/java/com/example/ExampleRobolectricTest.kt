package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AzkarDataSource
import com.example.data.QuranDataSource
import com.example.util.HijriCalendarHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("زاد المسلم", appName)
    }

    @Test
    fun verifyQuranSurahsCount() {
        assertEquals(114, QuranDataSource.surahs.size)
        val fatiha = QuranDataSource.getSurahById(1)
        assertNotNull(fatiha)
        assertEquals("الفَاتِحَة", fatiha?.nameArabic)
        assertEquals(7, fatiha?.versesCount)
    }

    @Test
    fun verifyAzkarCategoriesExist() {
        val morningAzkar = AzkarDataSource.getByCategory("الصباح")
        val eveningAzkar = AzkarDataSource.getByCategory("المساء")
        assertTrue(morningAzkar.isNotEmpty())
        assertTrue(eveningAzkar.isNotEmpty())
    }

    @Test
    fun verifyHijriCalendarCalculation() {
        val hijriDate = HijriCalendarHelper.getHijriDate()
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.month in 1..12)
        assertTrue(hijriDate.year >= 1440)
        assertTrue(hijriDate.monthName.isNotBlank())
    }
}
