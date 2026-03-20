// di/DatabaseModule.kt
package com.mykaradainam.di

import android.content.Context
import androidx.room.Room
import com.mykaradainam.data.local.AppDatabase
import com.mykaradainam.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mykaradainam.db"
        )
            .addCallback(AppDatabase.createCallback())
            .build()
    }

    @Provides fun provideRoomSessionDao(db: AppDatabase): RoomSessionDao = db.roomSessionDao()
    @Provides fun provideInvoiceItemDao(db: AppDatabase): InvoiceItemDao = db.invoiceItemDao()
    @Provides fun provideEquipmentDao(db: AppDatabase): EquipmentDao = db.equipmentDao()
    @Provides fun provideElectricityRateDao(db: AppDatabase): ElectricityRateDao = db.electricityRateDao()
}
