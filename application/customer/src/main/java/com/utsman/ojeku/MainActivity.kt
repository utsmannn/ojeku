package com.utsman.ojeku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.utsman.core.view.ComponentPlaygroundActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.main_text).setOnClickListener {
            ComponentPlaygroundActivity.launch(this)
        }
    }
}