package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranDataSource
import com.example.data.RecitersDataSource
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun AudioPlayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val playerState by viewModel.playerState.collectAsState()
    val downloadStatus by viewModel.downloadStatus.collectAsState()
    val downloadedSurahs by viewModel.downloadedSurahs.collectAsState(initial = emptyList())

    var selectedReciter by remember { mutableStateOf(playerState.currentReciter) }
    var selectedTab by remember { mutableStateOf(0) } // 0: All Surahs, 1: Downloaded

    val reciters = RecitersDataSource.reciters

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("audio_player_screen")
    ) {
        // 1. Reciter Selection Chips
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "اختر القارئ:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reciters) { reciter ->
                        val isSelected = selectedReciter.id == reciter.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedReciter = reciter },
                            label = {
                                Text(
                                    text = reciter.nameArabic,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // 2. Active Player Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("active_audio_player_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = IslamicGreenDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (playerState.isOfflineFile) IslamicGold else IslamicGreenContainer
                    ) {
                        Text(
                            text = if (playerState.isOfflineFile) "تشغيل محلي (دون إنترنت)" else "بث مباشر (MP3Quran)",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreenDark,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = "سورة رقم ${playerState.currentSurahId}",
                        style = MaterialTheme.typography.bodySmall.copy(color = IslamicGoldLight)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "سُورَةُ ${playerState.currentSurahName}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Text(
                    text = playerState.currentReciter.nameArabic,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = IslamicGoldLight,
                        fontSize = 14.sp
                    )
                )

                // Error Message if any
                if (!playerState.errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = playerState.errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Seek Progress Bar
                val totalMs = playerState.durationMs
                val currentMs = playerState.currentPositionMs
                val sliderPosition = if (totalMs > 0) (currentMs.toFloat() / totalMs).coerceIn(0f, 1f) else 0f

                Slider(
                    value = sliderPosition,
                    onValueChange = { frac ->
                        if (totalMs > 0) {
                            viewModel.seekAudio((frac * totalMs).toInt())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = IslamicGold,
                        activeTrackColor = IslamicGold,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentMs),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                    Text(
                        text = formatTime(totalMs),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Playback Controls Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Previous Surah
                    IconButton(
                        onClick = { viewModel.playPreviousSurah() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "السورة السابقة",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Play / Pause / Buffering button
                    Surface(
                        shape = CircleShape,
                        color = IslamicGold,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { viewModel.togglePlayPause() }
                            .testTag("audio_play_pause_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (playerState.isBuffering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = IslamicGreenDark,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playerState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                    tint = IslamicGreenDark,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // Next Surah
                    IconButton(
                        onClick = { viewModel.playNextSurah() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "السورة التالية",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // 3. Tab Row: All Surahs vs Downloaded
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = IslamicGreen
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("جميع السور (١١٤)", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "السور المحملة (${downloadedSurahs.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        // 4. Surah List with Streaming and Download buttons
        val surahsToDisplay = if (selectedTab == 0) {
            QuranDataSource.surahs
        } else {
            QuranDataSource.surahs.filter { s ->
                downloadedSurahs.any { it.surahId == s.id && it.reciterId == selectedReciter.id }
            }
        }

        if (selectedTab == 1 && surahsToDisplay.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "لا توجد سور محملة لهذا القارئ حتى الآن.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يمكنك تحميل أي سورة للاستماع إليها لاحقاً دون الحاجة إلى اتصال بالإنترنت.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("audio_surah_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(surahsToDisplay, key = { it.id }) { surah ->
                    val isCurrent = playerState.currentSurahId == surah.id && playerState.currentReciter.id == selectedReciter.id
                    val isPlaying = isCurrent && playerState.isPlaying
                    val downloadKey = "${surah.id}_${selectedReciter.id}"
                    val progress = downloadStatus[downloadKey]
                    val isDownloaded = downloadedSurahs.any { it.surahId == surah.id && it.reciterId == selectedReciter.id }

                    SurahAudioRowCard(
                        surah = surah,
                        isPlaying = isPlaying,
                        isDownloaded = isDownloaded,
                        downloadProgress = progress,
                        onPlayClick = { viewModel.playSurah(surah.id, selectedReciter) },
                        onDownloadClick = { viewModel.downloadSurah(surah.id, selectedReciter) },
                        onDeleteDownloadClick = { viewModel.deleteDownloadedSurah(surah.id, selectedReciter.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SurahAudioRowCard(
    surah: Surah,
    isPlaying: Boolean,
    isDownloaded: Boolean,
    downloadProgress: com.example.player.DownloadProgress?,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDeleteDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audio_surah_${surah.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) IslamicGreenContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Play Icon Button
                Surface(
                    shape = CircleShape,
                    color = if (isPlaying) IslamicGreen else IslamicGreenContainer,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPlayClick)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                            tint = if (isPlaying) Color.White else IslamicGreenDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "سُورَةُ ${surah.nameArabic}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${surah.revelationType} • ${surah.versesCount} آية",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Download status & action
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (downloadProgress != null && downloadProgress.isDownloading) {
                    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { downloadProgress.progressPercent / 100f },
                            modifier = Modifier.size(24.dp),
                            color = IslamicGreen,
                            strokeWidth = 2.dp
                        )
                    }
                } else if (isDownloaded) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IslamicGreenContainer,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = IslamicGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("محملة", fontSize = 11.sp, color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = onDeleteDownloadClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف التحميل",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    IconButton(onClick = onDownloadClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "تنزيل للاستماع دون إنترنت",
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Int): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
