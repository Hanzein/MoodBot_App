package com.farhanadi.moodbotapp.view.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.ActivityEditprofileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditprofileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.btnBackedtprofile.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.ProfileContainer, ProfileFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }
}