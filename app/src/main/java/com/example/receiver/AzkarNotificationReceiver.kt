package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.UserPreferences
import com.example.util.NotificationScheduler

class AzkarNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val prefs = UserPreferences(context)

        when (action) {
            NotificationScheduler.ACTION_MORNING_AZKAR -> {
                if (prefs.morningAzkarEnabled) {
                    showNotification(
                        context = context,
                        notificationId = 101,
                        title = "أذكار الصباح ☀️",
                        message = "أصبحنا وأصبح الملك لله.. نوّر يومك بذكر الله وحصّن نفسك بأذكار الصباح."
                    )
                    // Reschedule for next day
                    NotificationScheduler.scheduleMorningAlarm(context, prefs.morningAzkarHour, prefs.morningAzkarMinute)
                }
            }
            NotificationScheduler.ACTION_EVENING_AZKAR -> {
                if (prefs.eveningAzkarEnabled) {
                    showNotification(
                        context = context,
                        notificationId = 102,
                        title = "أذكار المساء 🌙",
                        message = "أمسينا وأمسى الملك لله.. اختم يومك بالسكينة والاطمئنان مع أذكار المساء."
                    )
                    // Reschedule for next day
                    NotificationScheduler.scheduleEveningAlarm(context, prefs.eveningAzkarHour, prefs.eveningAzkarMinute)
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-arm alarms after device restart
                if (prefs.morningAzkarEnabled) {
                    NotificationScheduler.scheduleMorningAlarm(context, prefs.morningAzkarHour, prefs.morningAzkarMinute)
                }
                if (prefs.eveningAzkarEnabled) {
                    NotificationScheduler.scheduleEveningAlarm(context, prefs.eveningAzkarHour, prefs.eveningAzkarMinute)
                }
            }
        }
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "zad_azkar_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تنبيهات أذكار زاد المسلم",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات أذكار الصباح والمساء اليومية"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "azkar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
