package com.utsman.utils.listener

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlin.reflect.KClass

fun <T: ActivityListener>Fragment.findActivityListener(): T? {
    return activity as? T
}

fun <T: FragmentListener>FragmentManager.findFragmentListener(tag: String): T? {
    return findFragmentByTag(tag) as? T
}