package com.smartmedicine.reminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartmedicine.reminder.data.database.AppDatabase
import com.smartmedicine.reminder.data.model.Medicine
import com.smartmedicine.reminder.data.model.MedicineLog
import com.smartmedicine.reminder.notification.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val medicineDao = database.medicineDao()

    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllActiveMedicines()

    private val _todayLogs = MutableStateFlow<List<MedicineLog>>(emptyList())
    val todayLogs: StateFlow<List<MedicineLog>> = _todayLogs.asStateFlow()

    private val _adherenceRate = MutableStateFlow(0f)
    val adherenceRate: StateFlow<Float> = _adherenceRate.asStateFlow()

    private val _takenCount = MutableStateFlow(0)
    val takenCount: StateFlow<Int> = _takenCount.asStateFlow()

    private val _missedCount = MutableStateFlow(0)
    val missedCount: StateFlow<Int> = _missedCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    init {
        loadTodayLogs()
        loadAdherenceStats()
    }

    private fun loadTodayLogs() {
        viewModelScope.launch {
            val today = getTodayMidnight()
            medicineDao.getLogsForDate(today).collect { logs ->
                _todayLogs.value = logs
            }
        }
    }

    fun loadAdherenceStats() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endDate = calendar.timeInMillis

            calendar.add(Calendar.DAY_OF_MONTH, -7)
            val startDate = getTodayMidnight() - (7 * 24 * 60 * 60 * 1000L)

            val taken = medicineDao.getTakenCountForRange(startDate, endDate)
            val missed = medicineDao.getMissedCountForRange(startDate, endDate)
            val total = medicineDao.getTotalCountForRange(startDate, endDate)

            _takenCount.value = taken
            _missedCount.value = missed
            _totalCount.value = total
            _adherenceRate.value = if (total > 0) (taken.toFloat() / total * 100) else 0f
        }
    }

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            val id = medicineDao.insertMedicine(medicine)
            val savedMedicine = medicine.copy(id = id)
            AlarmScheduler.scheduleMedicineAlarm(getApplication(), savedMedicine)

            // Create today's log entry
            val today = getTodayMidnight()
            medicineDao.insertLog(
                MedicineLog(
                    medicineId = id,
                    medicineName = medicine.name,
                    scheduledTime = getScheduledTime(medicine.timeHour, medicine.timeMinute),
                    status = MedicineLog.STATUS_UPCOMING,
                    date = today
                )
            )
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineDao.updateMedicine(medicine)
            AlarmScheduler.cancelMedicineAlarm(getApplication(), medicine)
            if (medicine.isActive) {
                AlarmScheduler.scheduleMedicineAlarm(getApplication(), medicine)
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            AlarmScheduler.cancelMedicineAlarm(getApplication(), medicine)
            medicineDao.deleteMedicine(medicine)
        }
    }

    fun markAsTaken(logId: Long) {
        viewModelScope.launch {
            medicineDao.updateLogStatus(logId, MedicineLog.STATUS_TAKEN, System.currentTimeMillis())
            loadAdherenceStats()
        }
    }

    fun markAsMissed(logId: Long) {
        viewModelScope.launch {
            medicineDao.updateLogStatus(
                logId,
                MedicineLog.STATUS_MISSED,
                System.currentTimeMillis()
            )
            loadAdherenceStats()
        }
    }

    suspend fun getMedicineById(id: Long): Medicine? {
        return medicineDao.getMedicineById(id)
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

    private fun getScheduledTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
