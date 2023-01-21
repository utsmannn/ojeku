package com.utsman.locationapi.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.utsman.locationapi.entity.LocationDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentLocationDao {
    @Insert
    suspend fun insertNewRecentLocation(locationDataEntity: LocationDataEntity)

    @Query("SELECT * FROM location_data")
    fun getRecentLocation(): Flow<List<LocationDataEntity>>

    @Query("SELECT COUNT(1) FROM location_data WHERE latLng = :latLng")
    suspend fun isExists(latLng: String): Boolean
}