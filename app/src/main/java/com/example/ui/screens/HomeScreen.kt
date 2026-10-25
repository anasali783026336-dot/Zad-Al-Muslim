package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val latestBookmark by viewModel.latestBookmark.collectAsState()
    val tasbihCount by viewModel.tasbihCount.collectAsState()
    val tasbihZikr by viewModel.tasbihZikr.collectAsState()
    val hijriDate = viewModel.getTodayHijriDate()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(IslamicGreen, IslamicGreenDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "زاد المسلم",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    )
                                )
                                Text(
                                    text = "«زادُ قلبك، ونورُ دربك»",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = IslamicGoldLight,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = IslamicGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = IslamicGoldLight,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Hijri & Gregorian Date pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.25f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${hijriDate.dayOfWeekName}، ${hijriDate.day} ${hijriDate.monthName} ${hijriDate.year} هـ",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = hijriDate.gregorianFormatted,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IslamicGold,
                                    modifier = Modifier.clickable {
                                        viewModel.navigateTo(Screen.CalendarView)
                                    }
                                ) {
                                    Text(
                                        text = "التقويم",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = IslamicGreenDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Reading Tracker / Bookmark Card (متابعة القراءة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reading_tracker_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IslamicGoldContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IslamicGold,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = IslamicGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "متابعة تلاوة القرآن الكريم",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (latestBookmark != null) {
                                    "${latestBookmark!!.surahName} - آية ${latestBookmark!!.ayahNumber}"
                                } else {
                                    "ابدأ تلاوتك اليومية واحفظ موضعك"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val surahId = latestBookmark?.surahId ?: 1
                            val ayah = latestBookmark?.ayahNumber ?: 1
                            viewModel.navigateTo(Screen.QuranReader(surahId, ayah))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("continue_reading_button")
                    ) {
                        Text(
                            text = if (latestBookmark != null) "متابعة" else "فتح المصحف",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 3. Mini Tasbih Quick Widget
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_tasbih_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تسبيح اليوم",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = tasbihZikr,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "المجموع: $tasbihCount تسبيحة",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { viewModel.incrementTasbih() },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(54.dp)
                                .testTag("home_quick_tasbih_btn"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "+1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Tasbih) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGreenContainer,
                                contentColor = IslamicGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("المسبحة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4. Section Grid Title
        item {
            Text(
                text = "الأقسام الرئيسية",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        // 5. Grid Cards (8 Core Sections)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeSectionCard(
                        title = "المصحف الشريف",
                        subtitle = "١١٤ سورة بالرسم العثماني",
                        icon = Icons.Default.MenuBook,
                        badge = "كامل",
                        containerColor = IslamicGreenContainer,
                        iconTint = IslamicGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.QuranIndex) }
                    )
                    HomeSectionCard(
                        title = "أذكار الصباح",
                        subtitle = "حصن المسلم وأدعية الصباح",
                        icon = Icons.Default.WbSunny,
                        badge = "يومي",
                        containerColor = IslamicGoldContainer,
                        iconTint = IslamicGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Azkar("الصباح")) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeSectionCard(
                        title = "أذكار المساء",
                        subtitle = "سكينة المساء وحفظ الليل",
                        icon = Icons.Default.NightsStay,
                        badge = "يومي",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        iconTint = IslamicGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Azkar("المساء")) }
                    )
                    HomeSectionCard(
                        title = "المسبحة الإلكترونية",
                        subtitle = "عداد تسبيح ذكي وتلقائي",
                        icon = Icons.Default.TouchApp,
                        badge = "تفاعلي",
                        containerColor = IslamicGreenContainer,
                        iconTint = IslamicGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Tasbih) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeSectionCard(
                        title = "التلاوات الصوتية",
                        subtitle = "ياسر الدوسري ونخبة القراء",
                        icon = Icons.Default.MusicNote,
                        badge = "صوتي",
                        containerColor = IslamicGoldContainer,
                        iconTint = IslamicGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.AudioPlayer) }
                    )
                    HomeSectionCard(
                        title = "التقويم الهجري",
                        subtitle = "المناسبات والأيام البيض",
                        icon = Icons.Default.CalendarMonth,
                        badge = "١٤٤٨ هـ",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        iconTint = IslamicGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.CalendarView) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeSectionCard(
                        title = "البحث في الأذكار",
                        subtitle = "البحث في الأدعية والأوراد",
                        icon = Icons.Default.Search,
                        badge = "سريع",
                        containerColor = IslamicGreenContainer,
                        iconTint = IslamicGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Azkar("الكل")) }
                    )
                    HomeSectionCard(
                        title = "تنبيهات الأذكار",
                        subtitle = "تخصيص مواعيد التذكير",
                        icon = Icons.Default.Notifications,
                        badge = "تنبيه",
                        containerColor = IslamicGoldContainer,
                        iconTint = IslamicGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.NotificationSettings) }
                    )
                }
            }
        }

        // 6. Spiritual Quote Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿ الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = IslamicGreenDark,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "سورة الرعد - آية ٢٨",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("section_card_${title}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}
