package com.smartmedicine.reminder.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartmedicine.reminder.data.model.Caregiver
import kotlinx.coroutines.flow.Flow

@Dao
interface CaregiverDao {

    @Query("SELECT * FROM caregivers ORDER BY name")
    fun getAllCaregivers(): Flow<List<Caregiver>>

    @Query("SELECT * FROM caregivers WHERE id = :id")
    suspend fun getCaregiverById(id: Long): Caregiver?

    @Query("SELECT * FROM caregivers WHERE notifyOnMissed = 1")
    suspend fun getCaregiversForMissedNotification(): List<Caregiver>

    @Query("SELECT * FROM caregivers WHERE notifyOnHealthAlert = 1")
    suspend fun getCaregiversForHealthAlert(): List<Caregiver>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaregiver(caregiver: Caregiver): Long

    @Update
    suspend fun updateCaregiver(caregiver: Caregiver)

    @Delete
    suspend fun deleteCaregiver(caregiver: Caregiver)

    @Query("DELETE FROM caregivers WHERE id = :id")
    suspend fun deleteCaregiverById(id: Long)
}
