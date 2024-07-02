package com.farhanadi.moodbotapp.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.view.calendar.CalendarFragment
import com.farhanadi.moodbotapp.view.chat.ChatFragment
import com.farhanadi.moodbotapp.view.home.HomeFragment
import com.farhanadi.moodbotapp.view.profile.ProfileFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1,"Home",R.drawable.ic_home_blue)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2,"Calendar",R.drawable.ic_calendar_blue)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3,"Chat",R.drawable.ic_chat_blue)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4,"Profile",R.drawable.ic_profile_blue)
        )


        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 -> {
                    replaceFragment(HomeFragment())
                }
                2 -> {
                    replaceFragment(CalendarFragment())
                }
                3 -> {
                    replaceFragment(ChatFragment())
                }
                4 -> {
                    replaceFragment(ProfileFragment())
                }
            }
        }

        // default Bottom Tab Selected
        replaceFragment(HomeFragment())
        bottomNavigation.show(1)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .commit()
    }
}