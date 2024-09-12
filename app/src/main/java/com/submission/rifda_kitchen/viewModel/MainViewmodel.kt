package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewmodel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    init {
        checkUser()
    }

    private fun checkUser() {
        _isUserLoggedIn.postValue(auth.currentUser != null)
    }
}