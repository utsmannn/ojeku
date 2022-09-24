package com.utsman.navigation

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.koin.androidx.fragment.android.replace
import kotlin.reflect.KClass

fun <T: Fragment>FragmentManager.attachFragment(
    frameLayout: FrameLayout,
    kClass: KClass<T>,
    backstackName: String? = null
): String {
    val instance = kClass.java.newInstance()
    val tag = kClass.qualifiedName.orEmpty()
    val newBackstackName = backstackName ?: tag
    this.beginTransaction()
        .add(frameLayout.id, instance, tag)
        .addToBackStack(newBackstackName)
        .commit()

    return tag
}

fun <T: Fragment>FragmentManager.replaceFragment(
    frameLayout: FrameLayout,
    kClass: KClass<T>,
    backstackName: String? = null
): String {
    val instance = kClass.java.newInstance()
    val tag = kClass.qualifiedName.orEmpty()
    val newBackstackName = backstackName ?: tag
    this.beginTransaction()
        .replace(frameLayout.id, instance, tag)
        .addToBackStack(newBackstackName)
        .commit()

    return tag
}