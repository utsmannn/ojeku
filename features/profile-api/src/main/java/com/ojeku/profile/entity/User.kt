package com.ojeku.profile.entity

data class User(
    val id: String ="",
    val username: String = "",
    val role: String = "",
    val fcmToken: String = "",
    val userExtra: Any
)