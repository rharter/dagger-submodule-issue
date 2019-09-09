package com.ryanharter.daggertest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ryanharter.daggertest.feature.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).apply {
            TextView(context).apply {
                text = "Loading..."
            }.also { addView(it) }
        })

        // artifical delay
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 3000L)
    }
}