package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.submission.rifda_kitchen.repository.AuthRepository

class AuthViewmodelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewmodel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
