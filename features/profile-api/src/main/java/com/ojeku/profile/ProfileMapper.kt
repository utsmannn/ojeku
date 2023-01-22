package com.ojeku.profile

import com.ojeku.profile.entity.User
import com.ojeku.profile.entity.UserResponse

object ProfileMapper {

    fun mapResponseToUser(response: UserResponse?): User {
        return User(
            id = response?.data?.id.orEmpty(),
            username = response?.data?.username.orEmpty(),
            role = response?.data?.role.orEmpty(),
            fcmToken = response?.data?.fcmToken.orEmpty(),
            userExtra = response?.data?.extra as Any
        )
    }
}