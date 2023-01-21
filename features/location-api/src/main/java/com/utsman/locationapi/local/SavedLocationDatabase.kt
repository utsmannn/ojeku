package com.utsman.locationapi.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utsman.locationapi.entity.LocationDataEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Database(entities = [LocationDataEntity::class], version = 1)
abstract class SavedLocationDatabase : RoomDatabase() {
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun recentLocationDao(): RecentLocationDao

    companion object : KoinComponent {
        fun build(context: Context): SavedLocationDatabase {
            return Room.databaseBuilder(context, SavedLocationDatabase::class.java, "location_data")
                .build()
        }

        fun instances(): SavedLocationDatabase {
            return get()
        }
    }
}