package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.rifda_kitchen.Helper.showDialog
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.admin.view.AdminActivity
import com.submission.rifda_kitchen.databinding.ActivityLoginBinding
import com.submission.rifda_kitchen.orderValidator.OrderValidatorActivity
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewmodel.isLoading.observe(this) {
            showLoading(it, binding.progressBar)
        }

        observeLoginResult()
        setupButtons()
    }

    private fun observeLoginResult() {
        authViewmodel.loginResult.observe(this) { success ->
            if (success) {
                authViewmodel.userRole.observe(this) { role ->
                    when (role) {
                        "User" -> navigateTo(MainActivity::class.java)
                        "Admin" -> navigateTo(AdminActivity::class.java)
                        "Order Validator" -> navigateTo(OrderValidatorActivity::class.java)
                        else -> showDialog(this, "Login Gagal", "Role tidak dikenal.")
                    }
                }
            } else {
                showDialog(this, "Login Gagal", "Email atau password salah. Silakan coba lagi.")
                Log.d(TAG, "Login Failed")
            }
        }
    }

    private fun setupButtons() {
        binding.signInButton.setOnClickListener {
            signIn()
            authViewmodel.isLoading.observe(this) {
                showLoading(it, binding.progressBar)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showDialog(this, "Error", "Email dan password tidak boleh kosong.")
                return@setOnClickListener
            }

            authViewmodel.signInWithEmail(email, password)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signIn() {
        authViewmodel.signIn(this)
    }

    private fun <T> navigateTo(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
