package com.farhanadi.moodbotapp.view.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.farhanadi.moodbotapp.databinding.ActivitySplashBinding
import com.farhanadi.moodbotapp.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private val splashTimeOut: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        animateLogo()

        // Using a Handler to delay the start of the main activity
        Handler().postDelayed({
            // Start the main activity after the specified delay
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // close the splash activity so it's not in the back stack
        }, splashTimeOut)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun animateLogo() {
        val fadeIn = ObjectAnimator.ofFloat(binding.logoApp, "alpha", 0f, 1f)
        val slideUp = ObjectAnimator.ofFloat(binding.logoApp, "translationY", 200f, 0f)
        fadeIn.duration = 1000
        slideUp.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, slideUp)

        animatorSet.start()
    }
}
