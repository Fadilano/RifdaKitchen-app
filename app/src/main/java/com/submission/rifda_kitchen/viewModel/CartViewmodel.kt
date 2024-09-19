package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class CartViewmodel(private val repository: Repository) : ViewModel() {

    private val _cartItemList = MutableLiveData<List<CartModel>?>()
    var cartItemList: MutableLiveData<List<CartModel>?> = _cartItemList

    private val _totalPrices = MutableLiveData<Int>()
    var totalPrices: LiveData<Int> = _totalPrices

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    private val _quantityUpdateMessage = MutableLiveData<String?>()
    var quantityUpdateMessage: MutableLiveData<String?> = _quantityUpdateMessage

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
    fun updateItemQuantity(cartModel: CartModel, quantity: Int) {
        viewModelScope.launch {
            repository.updateItemQuantity(cartModel, quantity) { success, message ->
                if (success) {
                    // Update the LiveData list by replacing the item with updated quantity
                    val updatedList = _cartItemList.value?.map {
                        if (it.name == cartModel.name) {
                            it.copy(quantity = quantity)
                        } else {
                            it
                        }
                    }
                    _cartItemList.postValue(updatedList)
                }
                _quantityUpdateMessage.postValue(message) // Post the message to show feedback in the UI
            }
        }
    }

    fun removeItem(cartModel: CartModel) {
        viewModelScope.launch {
            repository.removeItem(cartModel) { success, message ->
                if (success) {
                    val updatedList = _cartItemList.value?.filter { it.name != cartModel.name }
                    _cartItemList.postValue(updatedList)
                }
                _quantityUpdateMessage.postValue(message)
            }
        }
    }

    fun removeAllItems() {
        viewModelScope.launch {
            repository.removeAllItems { success, message ->
                if (success) {
                    _cartItemList.postValue(emptyList()) // Clear the list in ViewModel
                    _quantityUpdateMessage.postValue("All items removed from cart")
                } else {
                    _quantityUpdateMessage.postValue(message)
                }
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