package com.smartmedicine.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_logs")
data class MedicineLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String,
    val scheduledTime: Long,
    val actionTime: Long? = null,
    val status: String = STATUS_UPCOMING, // "taken", "missed", "snoozed", "upcoming"
    val date: Long // Day timestamp (midnight) for grouping
) {
    companion object {
        const val STATUS_TAKEN = "taken"
        const val STATUS_MISSED = "missed"
        const val STATUS_SNOOZED = "snoozed"
        const val STATUS_UPCOMING = "upcoming"
    }
}
