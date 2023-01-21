package com.utsman.locationapi.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_data")
data class LocationDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val latLng: String,
    val createdAt: Long
)