package com.submission.rifda_kitchen.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class DetailViewmodel(private val repository: Repository) : ViewModel() {

    private val _makananBeratLiveData = MutableLiveData<MakananBeratModel?>()
    val makananBeratLiveData: LiveData<MakananBeratModel?> = _makananBeratLiveData

    private val _makananRinganLiveData = MutableLiveData<MakananRinganModel?>()
    val makananRinganLiveData: LiveData<MakananRinganModel?> = _makananRinganLiveData

    private val _addToCartSuccess = MutableLiveData<String?>()
    val addToCartSuccess: LiveData<String?> = _addToCartSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun setMakananBerat(makananBerat: MakananBeratModel?) {
        _makananBeratLiveData.value = makananBerat
    }

    fun setMakananRingan(makananRingan: MakananRinganModel?) {
        _makananRinganLiveData.value = makananRingan
    }

    fun deleteOrderWithStockUpdate(userId: String, orderId: String, cartItems: List<CartModel>) {
        repository.deleteOrderWithStockUpdate(userId, orderId, cartItems) { success ->
            if (!success) {
                _errorMessage.value = "Failed to delete order and update stock"
            }
        }
    }


    fun updateOrderStatus(userId: String, orderId: String, status: String) {
        repository.cancelOrder(userId, orderId, status) { success ->
            if (!success) {
                _errorMessage.value = "Gagal membatalkan Pesanan"
            }
        }
    }


    fun addToCart(product: Any, quantity: Int) {
        viewModelScope.launch {
            try {
                repository.addToCart(product, quantity) { result ->
                    _addToCartSuccess.value = result
                }
            } catch (e: Exception) {
                _addToCartSuccess.value = "Error adding to cart"
            }
        }

    }
}
