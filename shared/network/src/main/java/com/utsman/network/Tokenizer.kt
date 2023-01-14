package com.utsman.network

import com.utsman.core.local.AppPreferences

class Tokenizer {
    private val preferences: AppPreferences = AppPreferences.instance

    var token: String
        get() = preferences.getString("token")
        set(value) = preferences.setString("token", value)
}