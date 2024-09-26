package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class OrderViewmodel(private val repository: Repository) : ViewModel() {

    private val _orderResult = MutableLiveData<Pair<Boolean, String>>()
    val orderResult: LiveData<Pair<Boolean, String>> = _orderResult



    fun saveOrder(order: OrderModel) {
        repository.saveOrder(order) { success, message ->
            _orderResult.value = Pair(success, message)
        }
    }

    fun updateUserDetails(userId: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.updateUserDetails(userId, phone, address)
        }
    }


}
