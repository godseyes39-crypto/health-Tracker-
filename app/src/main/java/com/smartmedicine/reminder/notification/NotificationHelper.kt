package com.smartmedicine.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.smartmedicine.reminder.MainActivity
import com.smartmedicine.reminder.R

object NotificationHelper {

    const val CHANNEL_ID = "medicine_reminder_channel"
    private const val CHANNEL_NAME = "Medicine Reminders"
    private const val CHANNEL_DESCRIPTION = "Notifications for medicine reminders"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showMedicineReminder(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dosage: String,
        logId: Long
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open the app
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            medicineId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark as Taken
        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("log_id", logId)
            putExtra("medicine_id", medicineId)
            putExtra("medicine_name", medicineName)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            (medicineId * 10 + 1).toInt(),
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze (10 minutes)
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("log_id", logId)
            putExtra("medicine_id", medicineId)
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (medicineId * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to take $medicineName")
            .setContentText("Dosage: $dosage")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("It's time to take your medicine: $medicineName\nDosage: $dosage")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "✓ Taken", takenPendingIntent)
            .addAction(0, "⏰ Snooze 10min", snoozePendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(medicineId.toInt(), notification)
    }

    fun cancelNotification(context: Context, medicineId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(medicineId.toInt())
    }
}
