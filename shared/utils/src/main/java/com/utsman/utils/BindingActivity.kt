package com.utsman.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BindingActivity<B: ViewBinding> : AppCompatActivity() {
    private var _binding: B? = null
    protected val binding get() = requireNotNull(_binding)

    abstract fun inflateBinding(): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)
        onCreateBinding(savedInstanceState)
    }

    abstract fun onCreateBinding(savedInstanceState: Bundle?)
}