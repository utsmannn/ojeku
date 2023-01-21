package com.utsman.locationapi

import com.google.android.gms.maps.model.LatLng
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.entity.LocationDataEntity
import com.utsman.locationapi.response.LocationResponse
import com.utsman.locationapi.response.ReverseLocationResponse
import com.utsman.locationapi.ui.toLatLng
import com.utsman.utils.orNol

object Mapper {

    fun mapLocationResponseToData(locationResponse: LocationResponse?): List<LocationData> {
        val mapperData: (LocationResponse.Data?) -> LocationData = {
            val name = it?.name.orEmpty()
            val address = "${it?.address?.distric}, ${it?.address?.country}, ${it?.address?.city}"
            val latLng = LatLng(it?.coordinate?.latitude.orNol(), it?.coordinate?.longitude.orNol())
            LocationData(name, address, latLng)
        }

        return locationResponse?.data?.map(mapperData).orEmpty()
    }

    fun mapReverseLocationResponseToData(reverseLocationResponse: ReverseLocationResponse?): LocationData {
        val data = reverseLocationResponse?.data ?: throw Throwable("Error response data")
        val name = data.name.orEmpty()
        val address = "${data.address?.distric}, ${data.address?.country}, ${data.address?.city}"
        val latLng = LatLng(data.coordinate?.latitude.orNol(), data.coordinate?.longitude.orNol())
        return LocationData(name, address, latLng)
    }

    fun mapEntityToData(locationDataEntity: LocationDataEntity): LocationData {
        return LocationData(
            name = locationDataEntity.name,
            address = locationDataEntity.address,
            latLng = locationDataEntity.latLng.toLatLng(),
            createdAt = locationDataEntity.createdAt
        )
    }

    fun mapDataToEntity(locationData: LocationData): LocationDataEntity {
        return LocationDataEntity(
            name = locationData.name,
            address = locationData.address,
            latLng = locationData.latLng.toString(),
            createdAt = locationData.createdAt
        )
    }
}