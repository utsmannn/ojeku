package com.utsman.locationapi

import com.utsman.locationapi.response.LocationResponse
import com.utsman.locationapi.response.ReverseLocationResponse
import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationWebServices {

    @GET(EndPoint.SEARCH)
    suspend fun searchLocation(
        @Query(QueryName.SEARCH_NAME) name: String,
        @Query(QueryName.SEARCH_COORDINATE) coordinate: String
    ): Response<LocationResponse>

    @GET(EndPoint.REVERSE)
    suspend fun reverseLocation(
        @Query(QueryName.SEARCH_COORDINATE) coordinate: String
    ): Response<ReverseLocationResponse>

    object EndPoint {
        internal const val SEARCH = "/api/location/search"
        internal const val REVERSE = "/api/location/reserve"
    }

    object QueryName {
        internal const val SEARCH_NAME = "name"
        internal const val SEARCH_COORDINATE = "coordinate"
    }

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()

        fun build(): LocationWebServices {
            return retrofitBuilder.build().create(LocationWebServices::class.java)
        }
    }
}