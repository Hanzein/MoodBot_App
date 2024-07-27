package com.farhanadi.moodbotapp.view.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.ActivitySignUpBinding
import com.farhanadi.moodbotapp.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAction() {
        // Handle click on "Login" text
        binding.tvTologin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle click on "Daftar" button
        binding.btnDaftarSignup.setOnClickListener {
            val username = binding.etUsernameSignup.text.toString().trim()
            val email = binding.etEmailSignup.text.toString().trim()
            val pass = binding.etPasswordSignup.text.toString()
            val confirmPass = binding.etConfirmPassSignup.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    registerUser(email, pass)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle click on eye icon for password field
        binding.etPasswordSignup.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                if (event.rawX >= (binding.etPasswordSignup.right - binding.etPasswordSignup.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(binding.etPasswordSignup)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        // Handle click on eye icon for confirm password field
        binding.etConfirmPassSignup.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                if (event.rawX >= (binding.etConfirmPassSignup.right - binding.etConfirmPassSignup.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(binding.etConfirmPassSignup)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Password is currently hidden, show it
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)
        } else {
            // Password is currently visible, hide it
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
        }
        // Move cursor to the end of the text
        editText.setSelection(editText.text.length)
    }

    private fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
