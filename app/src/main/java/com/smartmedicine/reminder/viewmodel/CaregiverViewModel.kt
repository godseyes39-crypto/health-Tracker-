package com.smartmedicine.reminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartmedicine.reminder.data.database.AppDatabase
import com.smartmedicine.reminder.data.model.Caregiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CaregiverViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val caregiverDao = database.caregiverDao()

    val allCaregivers: Flow<List<Caregiver>> = caregiverDao.getAllCaregivers()

    fun addCaregiver(caregiver: Caregiver) {
        viewModelScope.launch {
            caregiverDao.insertCaregiver(caregiver)
        }
    }

    fun updateCaregiver(caregiver: Caregiver) {
        viewModelScope.launch {
            caregiverDao.updateCaregiver(caregiver)
        }
    }

    fun deleteCaregiver(caregiver: Caregiver) {
        viewModelScope.launch {
            caregiverDao.deleteCaregiver(caregiver)
        }
    }
}
