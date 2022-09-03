package com.utsman.core.extensions

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.reflect.KClass

// val homeFragment = HomeFragment()
//
//        supportFragmentManager.beginTransaction()
//            .add(binding.mainFrame.id, homeFragment, HomeFragment::class.simpleName)
//            .commit()

fun <T: Fragment>FragmentActivity.attachFragment(
    frameLayout: FrameLayout,
    kClass: KClass<T>
): String {
    val instance = kClass.java.newInstance()
    val tag = kClass.qualifiedName.orEmpty()
    println(" ----- ASUUUU --> $tag")
    supportFragmentManager.beginTransaction()
        .add(frameLayout.id, instance, tag)
        .commit()

    return tag
}