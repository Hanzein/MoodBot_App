package com.farhanadi.moodbotapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.farhanadi.moodbotapp.databinding.FragmentProfileBinding
import com.farhanadi.moodbotapp.view.history.WeeklyHistoryActivity
import com.farhanadi.moodbotapp.view.language.LanguageActivity
import com.farhanadi.moodbotapp.view.notification.NotificationActivity

class ProfileFragment : Fragment(){

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupAction()

        return binding.root
    }

    private fun setupAction() {

        binding.btnEditprofile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        binding.btnBahasa.setOnClickListener {
            startActivity(Intent(requireActivity(), LanguageActivity::class.java))
        }

        binding.btnNotifikasi.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        binding.btnLaporan.setOnClickListener {
            startActivity(Intent(requireActivity(), WeeklyHistoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            //dialogLogout()
        }
    }

}