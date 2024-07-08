package com.farhanadi.moodbotapp.view.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farhanadi.moodbotapp.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}