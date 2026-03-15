package com.smartmedicine.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.smartmedicine.reminder.data.model.Medicine
import java.util.Calendar

object AlarmScheduler {

    fun scheduleMedicineAlarm(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule primary alarm
        scheduleAlarm(
            context,
            alarmManager,
            medicine,
            medicine.timeHour,
            medicine.timeMinute,
            medicine.id.toInt() * 100 + 1
        )

        // Schedule second alarm if twice daily
        if (medicine.secondTimeHour != null && medicine.secondTimeMinute != null) {
            scheduleAlarm(
                context,
                alarmManager,
                medicine,
                medicine.secondTimeHour,
                medicine.secondTimeMinute,
                medicine.id.toInt() * 100 + 2
            )
        }
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        medicine: Medicine,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicine_id", medicine.id)
            putExtra("medicine_name", medicine.name)
            putExtra("dosage", medicine.dosage)
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

            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Use repeating alarm for daily medicines
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
            pendingIntent
        )
    }

    fun cancelMedicineAlarm(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)

        // Cancel primary alarm
        val pendingIntent1 = PendingIntent.getBroadcast(
            context,
            medicine.id.toInt() * 100 + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent1)

        // Cancel secondary alarm
        val pendingIntent2 = PendingIntent.getBroadcast(
            context,
            medicine.id.toInt() * 100 + 2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent2)
    }

    fun scheduleSnooze(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dosage: String,
        delayMinutes: Int = 10
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicine_id", medicineId)
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
            putExtra("is_snooze", true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (medicineId * 100 + 99).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
            pendingIntent
        )
    }
}
