package com.smartmedicine.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "blood_pressure", "heart_rate", "blood_sugar", "weight"
    val value1: Float, // Primary value (systolic for BP, value for others)
    val value2: Float? = null, // Secondary value (diastolic for BP)
    val unit: String,
    val notes: String = "",
    val recordedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_BLOOD_PRESSURE = "blood_pressure"
        const val TYPE_HEART_RATE = "heart_rate"
        const val TYPE_BLOOD_SUGAR = "blood_sugar"
        const val TYPE_WEIGHT = "weight"
    }
}
