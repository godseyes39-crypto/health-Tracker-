package com.smartmedicine.reminder

import android.app.Application
import com.smartmedicine.reminder.notification.NotificationHelper

class MedicineReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
