package com.farhanadi.moodbotapp.view.login

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
import com.farhanadi.moodbotapp.databinding.ActivityLoginBinding
import com.farhanadi.moodbotapp.view.main.MainActivity
import com.farhanadi.moodbotapp.view.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAction() {
        binding.tvToSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Handle click on eye icon for password field
        binding.etPasswordLogin.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                if (event.rawX >= (binding.etPasswordLogin.right - binding.etPasswordLogin.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(binding.etPasswordLogin)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val pass = binding.etPasswordLogin.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login success, navigate to another activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finish current activity to prevent going back to it
                    } else {
                        // Login failed
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        if (isPasswordVisible) {
            // Password is currently visible, hide it
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
        } else {
            // Password is currently hidden, show it
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)
        }
        // Toggle flag
        isPasswordVisible = !isPasswordVisible
        // Move cursor to the end of the text
        editText.setSelection(editText.text.length)
    }
}
