// data/local/AppDatabase.kt
package com.mykaradainam.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mykaradainam.data.local.dao.*
import com.mykaradainam.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoomSessionEntity::class,
        InvoiceItemEntity::class,
        EquipmentEntity::class,
        ElectricityRateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomSessionDao(): RoomSessionDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun electricityRateDao(): ElectricityRateDao

    companion object {
        fun createCallback() = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-seed electricity rates (QĐ 1279/QĐ-BCT)
                db.execSQL("""
                    INSERT INTO electricity_rates (tierName, startHour, endHour, ratePerKwh) VALUES
                    ('Bình thường', 4, 17, 3152.0),
                    ('Cao điểm', 17, 20, 5422.0),
                    ('Bình thường', 20, 22, 3152.0),
                    ('Thấp điểm', 22, 4, 1918.0)
                """)
            }
        }
    }
}
