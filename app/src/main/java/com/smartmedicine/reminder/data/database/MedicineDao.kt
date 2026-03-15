package com.smartmedicine.reminder.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartmedicine.reminder.data.model.Medicine
import com.smartmedicine.reminder.data.model.MedicineLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines WHERE isActive = 1 ORDER BY timeHour, timeMinute")
    fun getAllActiveMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines ORDER BY timeHour, timeMinute")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): Medicine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    // Medicine Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicineLog): Long

    @Update
    suspend fun updateLog(log: MedicineLog)

    @Query("SELECT * FROM medicine_logs WHERE date = :date ORDER BY scheduledTime")
    fun getLogsForDate(date: Long): Flow<List<MedicineLog>>

    @Query("SELECT * FROM medicine_logs WHERE medicineId = :medicineId AND date = :date")
    suspend fun getLogForMedicineOnDate(medicineId: Long, date: Long): MedicineLog?

    @Query("SELECT * FROM medicine_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY scheduledTime")
    fun getLogsForDateRange(startDate: Long, endDate: Long): Flow<List<MedicineLog>>

    @Query("SELECT COUNT(*) FROM medicine_logs WHERE status = 'taken' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTakenCountForRange(startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM medicine_logs WHERE status = 'missed' AND date BETWEEN :startDate AND :endDate")
    suspend fun getMissedCountForRange(startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM medicine_logs WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCountForRange(startDate: Long, endDate: Long): Int

    @Query("UPDATE medicine_logs SET status = :status, actionTime = :actionTime WHERE id = :logId")
    suspend fun updateLogStatus(logId: Long, status: String, actionTime: Long)
}
