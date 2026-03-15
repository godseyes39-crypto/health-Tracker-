package com.smartmedicine.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.smartmedicine.reminder.data.database.AppDatabase
import com.smartmedicine.reminder.data.model.MedicineLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val logId = intent.getLongExtra("log_id", -1)
        val medicineId = intent.getLongExtra("medicine_id", -1)
        val medicineName = intent.getStringExtra("medicine_name") ?: ""

        if (logId == -1L) return

        val database = AppDatabase.getDatabase(context)

        when (intent.action) {
            "ACTION_TAKEN" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    database.medicineDao().updateLogStatus(
                        logId,
                        MedicineLog.STATUS_TAKEN,
                        System.currentTimeMillis()
                    )
                }
                NotificationHelper.cancelNotification(context, medicineId)
            }

            "ACTION_SNOOZE" -> {
                val dosage = intent.getStringExtra("dosage") ?: ""
                CoroutineScope(Dispatchers.IO).launch {
                    database.medicineDao().updateLogStatus(
                        logId,
                        MedicineLog.STATUS_SNOOZED,
                        System.currentTimeMillis()
                    )
                }
                NotificationHelper.cancelNotification(context, medicineId)
                AlarmScheduler.scheduleSnooze(context, medicineId, medicineName, dosage)
            }

            "ACTION_MISSED" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    database.medicineDao().updateLogStatus(
                        logId,
                        MedicineLog.STATUS_MISSED,
                        System.currentTimeMillis()
                    )
                    // Notify caregivers
                    notifyCaregivers(context, medicineName)
                }
                NotificationHelper.cancelNotification(context, medicineId)
            }
        }
    }

    private suspend fun notifyCaregivers(context: Context, medicineName: String) {
        val database = AppDatabase.getDatabase(context)
        val caregivers = database.caregiverDao().getCaregiversForMissedNotification()

        for (caregiver in caregivers) {
            try {
                val smsManager = context.getSystemService(SmsManager::class.java)
                smsManager.sendTextMessage(
                    caregiver.phoneNumber,
                    null,
                    "Medicine Reminder Alert: ${caregiver.name}, your patient has missed their medication: $medicineName. Please check on them.",
                    null,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
