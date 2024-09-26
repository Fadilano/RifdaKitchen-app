package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.UserModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class UserViewmodel(private val repository: Repository) : ViewModel() {

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: LiveData<UserModel?> = _currentUser

    fun getCurrentUser() {
        viewModelScope.launch {
            repository.fetchUserData { user ->
                _currentUser.postValue(user)
            }
        }
    }


}