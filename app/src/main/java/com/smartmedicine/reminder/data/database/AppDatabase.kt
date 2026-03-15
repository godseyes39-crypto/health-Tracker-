package com.smartmedicine.reminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartmedicine.reminder.data.model.Caregiver
import com.smartmedicine.reminder.data.model.HealthRecord
import com.smartmedicine.reminder.data.model.Medicine
import com.smartmedicine.reminder.data.model.MedicineLog

@Database(
    entities = [
        Medicine::class,
        MedicineLog::class,
        HealthRecord::class,
        Caregiver::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun caregiverDao(): CaregiverDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicine_reminder.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
