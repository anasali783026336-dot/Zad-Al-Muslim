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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IslamicEvent
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.util.HijriCalendarHelper
import com.example.viewmodel.MainViewModel

@Composable
fun HijriCalendarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val hijriOffset by viewModel.hijriOffset.collectAsState()
    val monthOffset by viewModel.calendarMonthOffset.collectAsState()

    val todayHijri = viewModel.getTodayHijriDate()
    val displayedHijri = viewModel.getDisplayedHijriDate()
    val events = viewModel.getEventsForCurrentDisplayedMonth()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Today Hijri Date Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_hijri_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "اليوم الحالي",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${todayHijri.dayOfWeekName}، ${todayHijri.day} ${todayHijri.monthName} ${todayHijri.year} هـ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = todayHijri.gregorianFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // 2. Moon Sighting Offset Adjustment
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = IslamicGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "تعديل التقويم الهجري",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "حسب رؤية الهلال (${if (hijriOffset > 0) "+$hijriOffset" else "$hijriOffset"} يوم)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IslamicGreenContainer,
                            modifier = Modifier.clickable { viewModel.adjustHijriOffset(-1) }
                        ) {
                            Text(
                                text = "-١",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = IslamicGreenDark)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { viewModel.adjustHijriOffset(-hijriOffset) }
                        ) {
                            Text(
                                text = "٠",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IslamicGreenContainer,
                            modifier = Modifier.clickable { viewModel.adjustHijriOffset(1) }
                        ) {
                            Text(
                                text = "+١",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = IslamicGreenDark)
                            )
                        }
                    }
                }
            }
        }

        // 3. Month Header & Navigation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.previousCalendarMonth() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الشهر السابق")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${displayedHijri.monthName} ${displayedHijri.year} هـ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGreenDark,
                                    fontSize = 18.sp
                                )
                            )
                            if (monthOffset != 0) {
                                Text(
                                    text = "اضغط للعودة إلى الشهر الحالي",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = IslamicGold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.clickable { viewModel.resetCalendarMonth() }
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.nextCalendarMonth() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "الشهر التالي")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Days Grid (1 to 30)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (col in 1..6) {
                                    val dayNum = row * 6 + col
                                    if (dayNum <= 30) {
                                        val isToday = monthOffset == 0 && todayHijri.day == dayNum
                                        val isWhiteDay = dayNum in 13..15
                                        val hasEvent = events.any { it.hijriDay == dayNum }

                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isToday -> IslamicGreen
                                                        hasEvent -> IslamicGoldContainer
                                                        isWhiteDay -> IslamicGreenContainer
                                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    }
                                                )
                                                .border(
                                                    width = if (isToday) 2.dp else if (isWhiteDay) 1.dp else 0.dp,
                                                    color = if (isToday) IslamicGold else if (isWhiteDay) IslamicGreen else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isToday || hasEvent || isWhiteDay) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 13.sp
                                                )
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(42.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(IslamicGreen, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اليوم الحالي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(IslamicGreenContainer, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الأيام البيض (١٣-١٥)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(IslamicGoldContainer, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مناسبة إسلامية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // 4. Islamic Events for this Month
        item {
            Text(
                text = "المناسبات الإسلامية في شهر ${displayedHijri.monthName}:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (events.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "لا توجد مناسبات رسمية كبرى مسجلة في هذا الشهر الهجري.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            items(events) { ev ->
                IslamicEventCard(event = ev)
            }
        }
    }
}

@Composable
fun IslamicEventCard(event: IslamicEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.title}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IslamicGoldContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${event.hijriDay}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreenDark
                            )
                        )
                        Text(
                            text = "هـ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = IslamicGreenDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreenDark,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}
