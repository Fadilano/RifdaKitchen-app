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

    fun signIn(context: Context) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogleIdToken(context)
            _loginResult.value = result
        }
    }

    fun signOUt(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
        }
    }
    fun getFirebaseUser() = authRepository.getFirebaseUser()


}