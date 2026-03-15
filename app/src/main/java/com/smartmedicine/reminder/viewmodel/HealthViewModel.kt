package com.smartmedicine.reminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartmedicine.reminder.data.database.AppDatabase
import com.smartmedicine.reminder.data.model.HealthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val healthRecordDao = database.healthRecordDao()

    val allRecords: Flow<List<HealthRecord>> = healthRecordDao.getAllRecords()

    private val _latestBP = MutableStateFlow<HealthRecord?>(null)
    val latestBP: StateFlow<HealthRecord?> = _latestBP.asStateFlow()

    private val _latestHeartRate = MutableStateFlow<HealthRecord?>(null)
    val latestHeartRate: StateFlow<HealthRecord?> = _latestHeartRate.asStateFlow()

    private val _latestBloodSugar = MutableStateFlow<HealthRecord?>(null)
    val latestBloodSugar: StateFlow<HealthRecord?> = _latestBloodSugar.asStateFlow()

    private val _latestWeight = MutableStateFlow<HealthRecord?>(null)
    val latestWeight: StateFlow<HealthRecord?> = _latestWeight.asStateFlow()

    init {
        loadLatestRecords()
    }

    fun loadLatestRecords() {
        viewModelScope.launch {
            _latestBP.value = healthRecordDao.getLatestRecordByType(HealthRecord.TYPE_BLOOD_PRESSURE)
            _latestHeartRate.value =
                healthRecordDao.getLatestRecordByType(HealthRecord.TYPE_HEART_RATE)
            _latestBloodSugar.value =
                healthRecordDao.getLatestRecordByType(HealthRecord.TYPE_BLOOD_SUGAR)
            _latestWeight.value = healthRecordDao.getLatestRecordByType(HealthRecord.TYPE_WEIGHT)
        }
    }

    fun addRecord(record: HealthRecord) {
        viewModelScope.launch {
            healthRecordDao.insertRecord(record)
            loadLatestRecords()
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            healthRecordDao.deleteRecord(record)
            loadLatestRecords()
        }
    }

    fun getRecordsByType(type: String): Flow<List<HealthRecord>> {
        return healthRecordDao.getRecordsByType(type)
    }
}
