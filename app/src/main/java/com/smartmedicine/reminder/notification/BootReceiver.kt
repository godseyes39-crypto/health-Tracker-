package com.smartmedicine.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartmedicine.reminder.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all active medicine alarms after device reboot
            val database = AppDatabase.getDatabase(context)

            CoroutineScope(Dispatchers.IO).launch {
                val medicines = database.medicineDao().getAllActiveMedicines().first()
                for (medicine in medicines) {
                    AlarmScheduler.scheduleMedicineAlarm(context, medicine)
                }
            }
        }
    }
}
