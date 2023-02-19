package com.utsman.locationapi.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.utsman.locationapi.entity.LocationDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSavedLocation(locationDataEntity: LocationDataEntity)

    @Query("SELECT * FROM location_data ORDER BY createdAt DESC")
    fun getSavedLocation(): Flow<List<LocationDataEntity>>

    @Query("DELETE FROM location_data WHERE latLng = :latLng")
    fun deleteLocation(latLng: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocation(locationDataEntity: LocationDataEntity)

    @Query("SELECT EXISTS(SELECT * FROM location_data WHERE latLng = :latLng)")
    suspend fun isExists(latLng: String): Boolean
}