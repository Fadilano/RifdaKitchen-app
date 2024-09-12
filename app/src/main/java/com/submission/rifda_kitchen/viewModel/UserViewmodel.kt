package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.submission.rifda_kitchen.model.UserModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class UserViewmodel(private val repository: Repository) : ViewModel() {

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: LiveData<UserModel?> = _currentUser

    fun getCurrentUser(){
        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser()
                user?.let {
                    val userModel = UserModel(
                        uid = it.uid,
                        name = it.displayName ?: "",
                        email = it.email ?: "",
                        photo_url = it.photoUrl?.toString() ?: ""
                    )
                    _currentUser.postValue(userModel)
                } ?: run {
                    _currentUser.postValue(null)
                }
            } catch (e: Exception) {
                _currentUser.postValue(null)
            }
        }
    }
}