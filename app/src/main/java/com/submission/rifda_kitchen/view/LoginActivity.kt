package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.submission.rifda_kitchen.databinding.ActivityLoginBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewmodel.loginResult.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.d(TAG, "Login Failed")
            }
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        lifecycleScope.launch {
            val isSuccess = authViewmodel.signIn(this@LoginActivity)
            if (isSuccess) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Log.d(TAG, "Sign-in failed")
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}