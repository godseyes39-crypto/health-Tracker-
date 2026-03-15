package com.smartmedicine.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val frequency: String, // "Daily", "Twice Daily", "Weekly", etc.
    val timeHour: Int, // 0-23
    val timeMinute: Int, // 0-59
    val secondTimeHour: Int? = null, // For "Twice Daily"
    val secondTimeMinute: Int? = null,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null
)

enum class MedicineFrequency(val displayName: String) {
    ONCE_DAILY("Once Daily"),
    TWICE_DAILY("Twice Daily"),
    THREE_TIMES_DAILY("Three Times Daily"),
    WEEKLY("Weekly"),
    AS_NEEDED("As Needed")
}
