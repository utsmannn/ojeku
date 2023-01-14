package com.utsman.profile

object ProfileMapper {

    fun mapResponseToUser(response: UserResponse?): User {
        return User(
            id = response?.data?.id.orEmpty(),
            username = response?.data?.username.orEmpty(),
            role = response?.data?.role.orEmpty(),
            fcmToken = response?.data?.fcmToken.orEmpty()
        )
    }
}