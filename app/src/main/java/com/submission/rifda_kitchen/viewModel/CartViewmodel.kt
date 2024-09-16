package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class CartViewmodel(private val repository: Repository) : ViewModel() {

    private val _cartItemList = MutableLiveData<List<CartModel>>()
    var cartItemList: LiveData<List<CartModel>> = _cartItemList

    private val _totalPrices = MutableLiveData<Int>()
    var totalPrices: LiveData<Int> = _totalPrices

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    fun fetchCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.fetchCartItem { list ->
                    _cartItemList.postValue(list)
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _isLoading.value = true
            }
        }
    }

    fun getTotalPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTotalPrice { Int ->
                    _totalPrices.postValue(Int)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = true
            }

        }
    }

}