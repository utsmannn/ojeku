package com.ojeku.profile.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

interface UserExtra

class CustomerExtra : UserExtra

data class DriverExtra(
    @SerializedName("vehicles_number")
    val vehiclesNumber: String,
    @SerializedName("is_active")
    val isActive: Boolean
) : UserExtra

fun Any.mapToDriverExtra(): DriverExtra {
    return Gson()
        .fromJson(this.toString(), object : TypeToken<DriverExtra>() {
        }.type)
}