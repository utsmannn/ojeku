package com.utsman.core.extensions

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

inline fun <reified T> T.toJson(): String {
    val gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

    return gson.toJson(this, object : TypeToken<T>() {}.type)
}

inline fun <reified T> String.fromJson(): T {
    val gson = GsonBuilder()
        .create()

    return gson.fromJson(this, object : TypeToken<T>() {}.type)
}