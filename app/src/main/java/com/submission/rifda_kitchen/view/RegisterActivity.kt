package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.rifda_kitchen.Helper.showDialog
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.databinding.ActivityRegisterBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etCutomerName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showDialog(this, "Error", "Semua kolom harus diisi.")
                return@setOnClickListener
            }

            if (password.length < 6) {
                showDialog(this, "Error", "Password harus lebih dari 6 karakter.")
                return@setOnClickListener
            }

            authViewmodel.signUpWithEmail(name, email, password) { success ->
                if (success) {
                    Log.d(TAG, "Registration Successful")
                    finish()
                } else {
                    showDialog(this, "Registrasi Gagal", "Terjadi kesalahan saat registrasi. Silakan coba lagi.")
                    Log.e(TAG, "Registration Failed")
                }
            }
        }

        authViewmodel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading, binding.progressBar)
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
