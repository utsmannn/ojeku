package com.utsman.navigation

import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

fun Context.intentTo(className: String) {
    val clazz = Class.forName(className)
    val intent = Intent(this, clazz)
    startActivity(intent)
}

fun Context.intentTo(clazz: KClass<*>) {
    val intent = Intent(this, clazz.java)
    startActivity(intent)
}