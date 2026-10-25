package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranDataSource
import com.example.model.Ayah
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

@Composable
fun QuranReaderScreen(
    surahId: Int,
    highlightAyah: Int = 1,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val surah = remember(surahId) { QuranDataSource.getSurahById(surahId) } ?: QuranDataSource.surahs.first()
    val verses = remember(surahId) { viewModel.getVerses(surahId) }
    val fontSizeScale by viewModel.quranFontSize.collectAsState()
    val latestBookmark by viewModel.latestBookmark.collectAsState()

    var selectedAyah by remember { mutableStateOf<Ayah?>(verses.find { it.numberInSurah == highlightAyah } ?: verses.firstOrNull()) }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostStateState() }
    val coroutineScope = rememberCoroutineScope()

    // Scroll to highlightAyah if requested
    LaunchedEffect(highlightAyah) {
        if (highlightAyah > 1 && highlightAyah <= verses.size) {
            listState.animateScrollToItem(highlightAyah + 1)
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("quran_reader_screen")) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Surah Frame Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("surah_reader_header"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IslamicGreenContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "سُورَةُ ${surah.nameArabic}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreenDark,
                                fontSize = 28.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${surah.revelationType} • عدد آياتها: ${surah.versesCount} • الجزء: ${surah.juzNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Controls: Font Zoom & Audio Player
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "حجم الخط:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        if (fontSizeScale > 18f) viewModel.setQuranFontSize(fontSizeScale - 2f)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.TextDecrease, contentDescription = "تصغير الخط", tint = IslamicGreen)
                                }
                                IconButton(
                                    onClick = {
                                        if (fontSizeScale < 36f) viewModel.setQuranFontSize(fontSizeScale + 2f)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.TextIncrease, contentDescription = "تكبير الخط", tint = IslamicGreen)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.playSurah(surah.id)
                                    viewModel.navigateTo(Screen.AudioPlayer)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استماع", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2. Basmalah (except for Surah At-Tawbah #9)
            if (surah.id != 9) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreenDark,
                                fontSize = (fontSizeScale + 2).sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            // 3. Verses with Uthmani text
            items(verses, key = { it.numberInSurah }) { ayah ->
                val isSelected = selectedAyah?.numberInSurah == ayah.numberInSurah
                val isBookmarked = latestBookmark?.surahId == surah.id && latestBookmark?.ayahNumber == ayah.numberInSurah

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedAyah = ayah }
                        .testTag("ayah_${ayah.numberInSurah}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            IslamicGoldContainer.copy(alpha = 0.6f)
                        } else if (isBookmarked) {
                            IslamicGreenContainer.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ayah Number Badge
                            Surface(
                                shape = CircleShape,
                                color = IslamicGreenContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${ayah.numberInSurah}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = IslamicGreenDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // Bookmark Button
                            IconButton(
                                onClick = {
                                    viewModel.saveBookmark(surah.id, ayah.numberInSurah)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "تم حفظ الآية (${ayah.numberInSurah}) من سورة ${surah.nameArabic} كعلامة قراءة."
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "حفظ علامة القراءة",
                                    tint = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Uthmani Verse Text - uniform dark color, correct diacritics
                        Text(
                            text = "${ayah.textUthmani} ﴿${ayah.numberInSurah}﴾",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = fontSizeScale.sp,
                                lineHeight = (fontSizeScale * 1.8).sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Justify
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // If selected, show Tafsir (تفسير ميسر)
                        AnimatedVisibility(visible = isSelected && ayah.tafsir.isNotBlank()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "التفسير الميسر:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = IslamicGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ayah.tafsir,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. Surah Navigation (Previous / Next Surah)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (surah.id > 1) {
                        OutlinedButton(
                            onClick = {
                                viewModel.navigateTo(Screen.QuranReader(surah.id - 1))
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("السورة السابقة")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (surah.id < 114) {
                        Button(
                            onClick = {
                                viewModel.navigateTo(Screen.QuranReader(surah.id + 1))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("السورة التالية")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState.hostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

class SnackbarHostStateState {
    val hostState = SnackbarHostState()
    suspend fun showSnackbar(message: String) {
        hostState.showSnackbar(message)
    }
}
