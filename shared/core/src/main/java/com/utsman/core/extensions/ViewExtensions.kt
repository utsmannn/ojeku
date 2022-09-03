package com.utsman.core.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.IdRes

fun <T: View> View.findIdByLazy(@IdRes id: Int): Lazy<T> {
    return lazy { findViewById(id) }
}

fun <T: View> Activity.findIdByLazy(@IdRes id: Int): Lazy<T> {
    return lazy { findViewById(id) }
}