package com.submission.rifda_kitchen.admin.ViewModel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.model.OrderModel
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {


    private val _paymentLinkCreated = MutableLiveData<String>()
    val paymentLinkCreated: LiveData<String> get() = _paymentLinkCreated

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _imageUploadStatus = MutableLiveData<Boolean>()
    val imageUploadStatus: LiveData<Boolean> get() = _imageUploadStatus




    fun getAllOrders(): LiveData<List<OrderModel>> {
        return repository.getAllOrders()
    }



    fun updateOrderStatus(userId: String, orderId: String, status: String, clearPaymentLink: Boolean) {
        repository.updateOrderStatus(userId, orderId, status, clearPaymentLink) { success ->
            if (!success) {
                _errorMessage.value = "Gagal memperbarui status pesanan"
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createPaymentLink(paymentRequest: PaymentLinkRequest) {
        repository.createPaymentLink(paymentRequest,
            onSuccess = { link ->
                _paymentLinkCreated.value = link
            },
            onFailure = { error ->
                _errorMessage.value = error.message
            }
        )
    }

    fun updatePaymentLink(userId: String, orderId: String, paymentLink: String) {
        repository.updatePaymentLink(userId, orderId, paymentLink) { success ->
        }
    }


    fun saveProductToFirebase(name: String, description: String, price: Int, category: String, imageUri: Uri, stock: Int) {
        _isLoading.value = true
        repository.uploadImage(imageUri) { imageUrl ->
            if (imageUrl != null) {
                val product = if (category == "makananberat") {
                    MakananBeratModel(name, description, price, imageUrl,stock)
                } else {
                    MakananRinganModel(name, description, price, imageUrl,stock)
                }
                repository.saveProductToDatabase(product, category) { success ->
                    _imageUploadStatus.value = success
                    _isLoading.value = false
                }
            } else {
                _errorMessage.value = "Failed to upload image"
                _isLoading.value = true
            }
        }
    }


}
