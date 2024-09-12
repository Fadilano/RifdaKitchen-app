package com.submission.rifda_kitchen.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.UserModel
import com.submission.rifda_kitchen.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewmodel(private val authRepository: AuthRepository) : ViewModel() {


    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult


    suspend fun signIn(context: Context): Boolean {
        return authRepository.signInWithGoogleIdToken(context)
    }

    fun signOUt(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
        }
    }


}