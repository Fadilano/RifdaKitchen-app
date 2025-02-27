package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class HistoryViewmodel(private val repository: Repository) : ViewModel() {

    private val _orderList = MutableLiveData<List<OrderModel>?>()
    val orderList: LiveData<List<OrderModel>?> = _orderList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchOrdersForUser(userUID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.fetchOrdersForUser(userUID) { orders ->
                    _orderList.postValue(orders) // Perbarui data di LiveData
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

}
