package com.submission.rifda_kitchen.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewmodel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun signIn(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.signInWithGoogleIdToken(context)
                _loginResult.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                _loginResult.value = false
                _isLoading.value = false
            }
        }
    }

    fun signOUt(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.signInWithEmail(email, password)
                _loginResult.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                _loginResult.value = false
                _isLoading.value = false
            }

        }
    }

    fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.signUpWithEmail(name, email, password)
                onComplete(result)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }


}