package com.utsman.navigation

import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KClass

fun Context.intentTo(className: String) {
    val clazz = Class.forName(className)
    val intent = Intent(this, clazz)
    startActivity(intent)
}

fun Context.intentTo(clazz: KClass<*>, onIntent: (Intent) -> Unit = {}) {
    val intent = Intent(this, clazz.java)
    onIntent.invoke(intent)
    startActivity(intent)
}

private object Navigation : KoinComponent {
    val activityNavigatorCustomer: ActivityNavigatorCustomer by inject()
    val activityNavigatorDriver: ActivityNavigatorDriver by inject()
}

fun activityNavigationCust(): ActivityNavigatorCustomer {
    return Navigation.activityNavigatorCustomer
}

fun activityNavigationDriver(): ActivityNavigatorDriver {
    return Navigation.activityNavigatorDriver
}