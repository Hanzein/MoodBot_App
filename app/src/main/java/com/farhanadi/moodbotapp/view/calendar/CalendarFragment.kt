package com.farhanadi.moodbotapp.view.calendar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.FragmentCalendarBinding
import com.farhanadi.moodbotapp.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class CalendarFragment : Fragment()  {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCalendarBinding.inflate(inflater, container, false)
        val checkLogin = binding.checkLogin
        firebaseAuth = FirebaseAuth.getInstance()
        val isLoggedIn = firebaseAuth?.currentUser != null
        if(isLoggedIn){
            checkLogin.visibility = View.GONE
        }else{
            checkLogin.visibility = View.VISIBLE
        }
        val logBtn= binding.btnToLogin
        logBtn.setOnClickListener{
            startActivity(Intent(requireActivity(),LoginActivity::class.java))
        }
        return binding.root
    }
}