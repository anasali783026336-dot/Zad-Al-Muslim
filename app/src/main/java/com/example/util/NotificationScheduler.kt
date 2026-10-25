package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.receiver.AzkarNotificationReceiver
import java.util.Calendar

object NotificationScheduler {

    const val ACTION_MORNING_AZKAR = "com.aistudio.zadalmuslim.ACTION_AZKAR_MORNING"
    const val ACTION_EVENING_AZKAR = "com.aistudio.zadalmuslim.ACTION_AZKAR_EVENING"

    const val REQUEST_CODE_MORNING = 1001
    const val REQUEST_CODE_EVENING = 1002

    fun setupNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "zad_azkar_channel",
                "تنبيهات أذكار زاد المسلم",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات أذكار الصباح والمساء اليومية"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleMorningAlarm(context: Context, hour: Int, minute: Int) {
        scheduleAlarm(context, ACTION_MORNING_AZKAR, REQUEST_CODE_MORNING, hour, minute)
    }

    fun scheduleEveningAlarm(context: Context, hour: Int, minute: Int) {
        scheduleAlarm(context, ACTION_EVENING_AZKAR, REQUEST_CODE_EVENING, hour, minute)
    }

    private fun scheduleAlarm(
        context: Context,
        action: String,
        requestCode: Int,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AzkarNotificationReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In case exact alarm permission is restricted on Android 12+, fallback safely
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, action: String, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AzkarNotificationReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun triggerImmediateTestNotification(context: Context) {
        setupNotificationChannels(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "azkar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "zad_azkar_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("تنبيه تجريبي: زاد المسلم 🕌")
            .setContentText("أهلاً بك! نظام التنبيهات يعمل بنجاح. ألا بذكر الله تطمئن القلوب.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("أهلاً بك! نظام التنبيهات يعمل بنجاح على هاتفك. سيصلك تذكير يومي بأذكار الصباح والمساء في المواعيد التي تحددها."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(999, notification)
    }
}
