package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: Int = 1,
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_azkar")
data class FavoriteZikrEntity(
    @PrimaryKey val zikrId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloaded_surahs", primaryKeys = ["surahId", "reciterId"])
data class DownloadedSurahEntity(
    val surahId: Int,
    val reciterId: String,
    val surahName: String,
    val reciterName: String,
    val localFilePath: String,
    val fileSizeBytes: Long = 0L,
    val downloadedAt: Long = System.currentTimeMillis()
)
