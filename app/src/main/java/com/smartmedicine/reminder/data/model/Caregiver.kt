package com.smartmedicine.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caregivers")
data class Caregiver(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val notifyOnMissed: Boolean = true,
    val notifyOnHealthAlert: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
