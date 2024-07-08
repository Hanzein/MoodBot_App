package com.farhanadi.moodbotapp.view.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farhanadi.moodbotapp.databinding.ActivityLanguageBinding

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupView()
//        setupAction()
    }
}