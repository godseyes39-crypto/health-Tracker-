package com.smartmedicine.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartmedicine.reminder.data.database.AppDatabase
import com.smartmedicine.reminder.data.model.MedicineLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra("medicine_id", -1)
        val medicineName = intent.getStringExtra("medicine_name") ?: return
        val dosage = intent.getStringExtra("dosage") ?: ""

        if (medicineId == -1L) return

        val database = AppDatabase.getDatabase(context)
        val medicineDao = database.medicineDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Create a log entry for this reminder
            val today = getTodayMidnight()
            val existingLog = medicineDao.getLogForMedicineOnDate(medicineId, today)

            val logId = if (existingLog == null) {
                medicineDao.insertLog(
                    MedicineLog(
                        medicineId = medicineId,
                        medicineName = medicineName,
                        scheduledTime = System.currentTimeMillis(),
                        status = MedicineLog.STATUS_UPCOMING,
                        date = today
                    )
                )
            } else {
                existingLog.id
            }

            // Show notification
            NotificationHelper.showMedicineReminder(
                context,
                medicineId,
                medicineName,
                dosage,
                logId
            )

            // Reschedule for next day (non-snooze alarms)
            val isSnooze = intent.getBooleanExtra("is_snooze", false)
            if (!isSnooze) {
                val medicine = medicineDao.getMedicineById(medicineId)
                if (medicine != null && medicine.isActive) {
                    AlarmScheduler.scheduleMedicineAlarm(context, medicine)
                }
            }
        }
    }

    private fun getTodayMidnight(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
