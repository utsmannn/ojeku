package com.utsman.core.extensions

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun <T : View> View.findIdByLazy(@IdRes id: Int): Lazy<T> {
    return lazy { findViewById(id) }
}

fun <T : View> Activity.findIdByLazy(@IdRes id: Int): Lazy<T> {
    return lazy { findViewById(id) }
}

fun EditText.changes(): Flow<String> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }
}

fun Context.toast(message: String?) =
    Toast.makeText(this, message.orEmpty(), Toast.LENGTH_SHORT).show()

fun ViewGroup.inflateRoot(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


private class ViewPagerAdapter(fragmentManager: FragmentManager, private vararg val fragments: Fragment) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}

fun ViewPager.setup(fragmentManager: FragmentManager, vararg fragment: Fragment) {
    val pagerAdapter = ViewPagerAdapter(fragmentManager, *fragment)
    adapter = pagerAdapter
}