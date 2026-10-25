package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ZadDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks WHERE id = 1 LIMIT 1")
    fun getLatestBookmark(): Flow<BookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBookmark(bookmark: BookmarkEntity)

    // Favorite Azkar
    @Query("SELECT zikrId FROM favorite_azkar")
    fun getFavoriteZikrIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteZikr(item: FavoriteZikrEntity)

    @Query("DELETE FROM favorite_azkar WHERE zikrId = :zikrId")
    suspend fun removeFavoriteZikr(zikrId: Int)

    // Downloaded Surahs
    @Query("SELECT * FROM downloaded_surahs ORDER BY surahId ASC")
    fun getAllDownloadedSurahs(): Flow<List<DownloadedSurahEntity>>

    @Query("SELECT * FROM downloaded_surahs WHERE surahId = :surahId AND reciterId = :reciterId LIMIT 1")
    suspend fun getDownloadedSurah(surahId: Int, reciterId: String): DownloadedSurahEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedSurah(surah: DownloadedSurahEntity)

    @Query("DELETE FROM downloaded_surahs WHERE surahId = :surahId AND reciterId = :reciterId")
    suspend fun deleteDownloadedSurah(surahId: Int, reciterId: String)
}
