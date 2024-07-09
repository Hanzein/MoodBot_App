package com.farhanadi.moodbotapp.view.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.ActivityLanguageBinding
import com.farhanadi.moodbotapp.view.profile.ProfileFragment


class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.ProfileContainer, ProfileFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }
}
