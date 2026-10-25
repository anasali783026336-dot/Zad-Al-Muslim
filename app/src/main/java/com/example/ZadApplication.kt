package com.example

import android.app.Application
import com.example.data.local.UserPreferences
import com.example.data.local.ZadDatabase
import com.example.util.NotificationScheduler

class ZadApplication : Application() {

    lateinit var database: ZadDatabase
        private set

    lateinit var userPreferences: UserPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        database = ZadDatabase.getInstance(this)
        userPreferences = UserPreferences(this)

        // Setup notification channel
        NotificationScheduler.setupNotificationChannels(this)

        // Schedule alarms if enabled
        if (userPreferences.morningAzkarEnabled) {
            NotificationScheduler.scheduleMorningAlarm(
                this,
                userPreferences.morningAzkarHour,
                userPreferences.morningAzkarMinute
            )
        }
        if (userPreferences.eveningAzkarEnabled) {
            NotificationScheduler.scheduleEveningAlarm(
                this,
                userPreferences.eveningAzkarHour,
                userPreferences.eveningAzkarMinute
            )
        }
    }
}
