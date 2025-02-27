package com.submission.rifda_kitchen.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.submission.rifda_kitchen.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewmodel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> get() = _userRole

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    init {
        checkUser()
    }

    private fun checkUser() {
        _isUserLoggedIn.postValue(auth.currentUser != null)
    }

    fun signIn(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.signInWithGoogleIdToken(context)
                if (result) {
                    val user = authRepository.getFirebaseUser()
                    user?.let {
                        val role = authRepository.getUserRole(it.uid)
                        _userRole.postValue(role)
                    }
                }
                _loginResult.postValue(result)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _loginResult.postValue(false)
                _isLoading.postValue(false)
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
                if (result) {
                    val user = authRepository.getFirebaseUser()
                    user?.let {
                        val role = authRepository.getUserRole(it.uid)
                        _userRole.postValue(role)
                    }
                }
                _loginResult.postValue(result)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _loginResult.postValue(false)
                _isLoading.postValue(false)
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