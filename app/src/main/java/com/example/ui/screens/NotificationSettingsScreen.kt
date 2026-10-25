package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun NotificationSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val morningEnabled by viewModel.morningEnabled.collectAsState()
    val morningHour by viewModel.morningHour.collectAsState()
    val morningMinute by viewModel.morningMinute.collectAsState()

    val eveningEnabled by viewModel.eveningEnabled.collectAsState()
    val eveningHour by viewModel.eveningHour.collectAsState()
    val eveningMinute by viewModel.eveningMinute.collectAsState()

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    var showMorningTimeDialog by remember { mutableStateOf(false) }
    var showEveningTimeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("notification_settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Android Permission Status Card
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = IslamicGoldContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = IslamicGreenDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إذن الإشعارات مطلوب",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGreenDark
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "لضمان وصول تنبيهات أذكار الصباح والمساء في أوقاتها المحددة، يُرجى منح التطبيق إذن الإشعارات.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تفعيل إذن الإشعارات")
                        }
                    }
                }
            }
        }

        // 2. Morning Azkar Reminder Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("morning_notification_card"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تنبيه أذكار الصباح",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "تذكير يومي لبدء اليوم بذكر الله",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Switch(
                            checked = morningEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.updateMorningNotification(isChecked, morningHour, morningMinute)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = IslamicGreen
                            )
                        )
                    }

                    if (morningEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IslamicGreenContainer.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "وقت التنبيه: ${formatTime(morningHour, morningMinute)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = IslamicGreenDark)
                                )
                            }

                            Button(
                                onClick = { showMorningTimeDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("تغيير الوقت", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Evening Azkar Reminder Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evening_notification_card"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = IslamicGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تنبيه أذكار المساء",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "تذكير يومي لختام اليوم بالسكينة",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Switch(
                            checked = eveningEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.updateEveningNotification(isChecked, eveningHour, eveningMinute)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = IslamicGreen
                            )
                        )
                    }

                    if (eveningEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IslamicGreenContainer.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "وقت التنبيه: ${formatTime(eveningHour, eveningMinute)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = IslamicGreenDark)
                                )
                            }

                            Button(
                                onClick = { showEveningTimeDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("تغيير الوقت", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 4. Test Notification Button Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اختبار نظام التنبيهات",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اضغط على الزر أدناه لإرسال إشعار فوري وتجربة عمل الإشعارات على جهازك الآن.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.testNotification() },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("send_test_notification_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = IslamicGreenDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إرسال إشعار تجريبي الآن",
                            color = IslamicGreenDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 5. Info Note Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = IslamicGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "يتم حفظ التنبيهات محلياً وإعادة جدولتها تلقائياً عند إعادة تشغيل الهاتف.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }

    // Time Pickers Dialogs
    if (showMorningTimeDialog) {
        SimpleTimePickerDialog(
            title = "حدد وقت أذكار الصباح",
            initialHour = morningHour,
            initialMinute = morningMinute,
            onDismiss = { showMorningTimeDialog = false },
            onConfirm = { h, m ->
                viewModel.updateMorningNotification(morningEnabled, h, m)
                showMorningTimeDialog = false
            }
        )
    }

    if (showEveningTimeDialog) {
        SimpleTimePickerDialog(
            title = "حدد وقت أذكار المساء",
            initialHour = eveningHour,
            initialMinute = eveningMinute,
            onDismiss = { showEveningTimeDialog = false },
            onConfirm = { h, m ->
                viewModel.updateEveningNotification(eveningEnabled, h, m)
                showEveningTimeDialog = false
            }
        )
    }
}

@Composable
fun SimpleTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(selectedHour, selectedMinute),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Hour controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الساعة:")
                    Row {
                        Button(
                            onClick = { selectedHour = if (selectedHour > 0) selectedHour - 1 else 23 },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenContainer, contentColor = IslamicGreenDark),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("-") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { selectedHour = (selectedHour + 1) % 24 },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenContainer, contentColor = IslamicGreenDark),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("+") }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Minute controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الدقيقة:")
                    Row {
                        Button(
                            onClick = { selectedMinute = if (selectedMinute >= 5) selectedMinute - 5 else 55 },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenContainer, contentColor = IslamicGreenDark),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("-5") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { selectedMinute = (selectedMinute + 5) % 60 },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenContainer, contentColor = IslamicGreenDark),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("+5") }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val amPm = if (isPm) "م" else "ص"
    return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
}
