package com.farhanadi.moodbotapp.view.profile

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.FragmentProfileBinding
import com.farhanadi.moodbotapp.view.history.WeeklyHistoryActivity
import com.farhanadi.moodbotapp.view.language.LanguageActivity
import com.farhanadi.moodbotapp.view.login.LoginActivity
import com.farhanadi.moodbotapp.view.notification.NotificationActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(){

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var logoutDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()


        // Check if user is logged in
        if (firebaseAuth.currentUser == null) {
            // User not logged in, show login button and hide profile actions
            binding.btnLoginProfile.visibility = View.VISIBLE
            binding.btnEditprofile.visibility = View.GONE
            binding.btnBahasa.visibility = View.GONE
            binding.btnNotifikasi.visibility = View.GONE
            binding.btnLaporan.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
        } else {
            // User logged in, hide login button and show profile actions
            binding.btnLoginProfile.visibility = View.GONE
        }
        val btnLog= binding.btnLogout
        val isLoggedIn = firebaseAuth?.currentUser != null
        btnLog.text = if (isLoggedIn) "Logout" else "Login"
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
            if(binding.btnLogout.text == "Logout"){
                showLogoutConfirmationDialog()
            }else{
                startActivity(Intent(requireActivity(),LoginActivity::class.java))
            }
        }
    }


    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.costum_dialog_logout, null)
        builder.setView(dialogView)

        val btnYes = dialogView.findViewById<View>(R.id.btn_yes)
        val btnNo = dialogView.findViewById<View>(R.id.btn_no)

        btnYes.setOnClickListener {
            logout()
            logoutDialog?.dismiss()
        }

        btnNo.setOnClickListener {
            logoutDialog?.dismiss()
        }

        // Set the window background to transparent
        logoutDialog = builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }

        logoutDialog?.show()
    }

    private fun logout() {
        firebaseAuth.signOut()
        // After signing out, navigate to the login screen or any other initial screen
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logoutDialog?.dismiss() // Dismiss the dialog to prevent window leaks
    }
}