package com.utsman.core.local

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppPreferences(private val context: Context) {
    private val preferences = context.getSharedPreferences("ojeku", Context.MODE_PRIVATE)

    fun getString(key: String): String {
        return preferences.getString(key, "").orEmpty()
    }

    fun setString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    companion object : KoinComponent {
        val instance: AppPreferences by inject()
    }
}