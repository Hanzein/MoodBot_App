package com.farhanadi.moodbotapp.view.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.ActivityNotificationBinding
import com.farhanadi.moodbotapp.view.profile.ProfileFragment

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
    }

    private fun setupAction() {
        binding.btnBacknotif.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.ProfileContainer, ProfileFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }
}