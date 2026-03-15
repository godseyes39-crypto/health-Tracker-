package com.smartmedicine.reminder.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartmedicine.reminder.data.model.HealthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Query("SELECT * FROM health_records ORDER BY recordedAt DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE type = :type ORDER BY recordedAt DESC")
    fun getRecordsByType(type: String): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE type = :type ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestRecordByType(type: String): HealthRecord?

    @Query("SELECT * FROM health_records WHERE type = :type ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecentRecordsByType(type: String, limit: Int): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getRecordById(id: Long): HealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord): Long

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("DELETE FROM health_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT * FROM health_records WHERE recordedAt BETWEEN :startDate AND :endDate ORDER BY recordedAt DESC")
    fun getRecordsForDateRange(startDate: Long, endDate: Long): Flow<List<HealthRecord>>
}
