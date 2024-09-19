package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository

class OrderViewmodel(private val repository: Repository) : ViewModel() {

    private val _orderResult = MutableLiveData<Pair<Boolean, String>>()
    val orderResult: LiveData<Pair<Boolean, String>> = _orderResult

    fun saveOrder(order: OrderModel) {
        repository.saveOrder(order) { success, message ->
            _orderResult.value = Pair(success, message)
        }
    }
}