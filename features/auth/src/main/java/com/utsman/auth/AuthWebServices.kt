package com.utsman.auth

import com.utsman.auth.request.SignRequest
import com.utsman.auth.response.SignInResponse
import com.utsman.auth.response.SignUpResponse
import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthWebServices {

    @POST(EndPoint.LOGIN)
    suspend fun login(
        @Body request: SignRequest
    ): Response<SignInResponse>

    @POST(EndPoint.REGISTER_DRIVER)
    suspend fun registerDriver(
        @Body request: Any
    ): Response<SignUpResponse>

    @POST(EndPoint.REGISTER_CUSTOMER)
    suspend fun registerCustomer(
        @Body request: SignRequest
    ): Response<SignUpResponse>

    object EndPoint {
        private const val PREFIX = "/api/user"
        const val LOGIN = "$PREFIX/login"
        const val REGISTER_DRIVER = "$PREFIX/driver/register"
        const val REGISTER_CUSTOMER = "$PREFIX/customer/register"
    }

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()

        fun build(): AuthWebServices {
            return retrofitBuilder.build().create(AuthWebServices::class.java)
        }
    }
}