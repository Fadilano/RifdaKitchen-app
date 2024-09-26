package com.submission.rifda_kitchen.admin.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.model.OrderModel

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> get() = _orders

    private val _confirmationStatusUpdated = MutableLiveData<Boolean>()
    val confirmationStatusUpdated: LiveData<Boolean> get() = _confirmationStatusUpdated

    private val _paymentLinkCreated = MutableLiveData<String>()
    val paymentLinkCreated: LiveData<String> get() = _paymentLinkCreated

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getAllOrders(): LiveData<List<OrderModel>> {
        return repository.getAllOrders()
    }

    fun getTotalOrders(): LiveData<Long> {
        return repository.getTotalOrders()
    }

    fun updateConfirmationStatus(userId: String, orderId: String, confirmed: Boolean) {
        repository.updateConfirmationStatus(userId, orderId, confirmed) { success ->
            _confirmationStatusUpdated.value = success
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
            // No need for specific handling here, could trigger some UI update in the view
        }
    }
}
