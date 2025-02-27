package com.submission.rifda_kitchen.admin.ViewModel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.model.ProductModel
import com.submission.rifda_kitchen.model.UserModel
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

    private val _orderStatusUpdated = MutableLiveData<Boolean>()
    val orderStatusUpdated: LiveData<Boolean> get() = _orderStatusUpdated

    private val _productDeleted = MutableLiveData<Boolean>()
    val productDeleted: LiveData<Boolean> get() = _productDeleted

    private val _productUpdated = MutableLiveData<Boolean>()
    val productUpdated: LiveData<Boolean> get() = _productUpdated

    private val _monthlyOrders = MutableLiveData<Map<String, Int>>()
    val monthlyOrders: LiveData<Map<String, Int>> get() = _monthlyOrders

    private val _users = MutableLiveData<List<UserModel>>()
    val users: LiveData<List<UserModel>> get() = _users

    private val _todayOrdersCount = MutableLiveData<Int>()
    val todayOrdersCount: LiveData<Int> get() = _todayOrdersCount

    fun fetchTodayOrdersCount() {
        repository.getTodayOrdersCount { count ->
            _todayOrdersCount.postValue(count)
        }
    }


    fun deleteOrder(userId: String, orderId: String, cartItems: List<CartModel>) {
        repository.deleteOrderWithStockUpdate(userId, orderId, cartItems) { success ->
            if (!success) {
                _errorMessage.value = "Failed to delete order and update stock"
            }
        }
    }



    fun updateUserRole(userId: String, newRole: String) {
        _isLoading.value = true
        repository.updateUserRole(userId, newRole) { success ->
            _isLoading.value = false
            if (!success) {
                _errorMessage.value = "Failed to update user role"
            }
        }
    }


    fun fetchUsers() {
        repository.getAllUsers().observeForever {
            _users.value = it
        }
    }


    fun fetchMonthlyOrders() {
        repository.getOrdersPerMonth().observeForever {
            _monthlyOrders.value = it
        }
    }

    fun deleteOrderWithStockUpdate(userId: String, orderId: String, cartItems: List<CartModel>) {
        repository.deleteOrderWithStockUpdate(userId, orderId, cartItems) { success ->
            if (!success) {
                _errorMessage.value = "Failed to delete order and update stock"
            }
        }
    }



    fun getAllOrders(): LiveData<List<OrderModel>> {
        val allOrdersLiveData = MutableLiveData<List<OrderModel>>()
        repository.getAllOrders().observeForever { orders ->
            val sortedOrders = orders.sortedWith(
                compareByDescending<OrderModel> { it.date }
                    .thenByDescending { it.time }
            )
            allOrdersLiveData.postValue(sortedOrders)
        }
        return allOrdersLiveData
    }


    fun updateOrderStatus(userId: String, orderId: String, status: String, clearPaymentLink: Boolean) {
        repository.updateOrderStatus(userId, orderId, status, clearPaymentLink) { success ->
            if (success) {
                _orderStatusUpdated.value = true
            } else {
                _errorMessage.value = "Failed to update order status"
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
                    MakananBeratModel(productId = "", name = name, description = description, price = price, image_url = imageUrl, stock = stock)
                } else {
                    MakananRinganModel(productId = "", name = name, description = description, price = price, image_url = imageUrl, stock = stock)
                }
                repository.saveProductToDatabase(product, category) { success, message ->
                    _imageUploadStatus.value = success
                    _isLoading.value = false
                    if (!success) {
                        _errorMessage.value = message
                    }
                }
            } else {
                _errorMessage.value = "Failed to upload image"
                _isLoading.value = false
            }
        }
    }
    fun updateProduct(
        productId: String,
        updatedProduct: ProductModel,
        category: String,
        clearImage: Boolean = false
    ) {
        _isLoading.value = true
        repository.updateProduct(productId, updatedProduct, category, clearImage) { success ->
            _isLoading.value = false
            _productUpdated.value = success
            if (!success) _errorMessage.value = "Failed to update product"
        }
    }

    fun updateProductWithImage(
        productId: String,
        updatedProduct: ProductModel,
        category: String,
        newImageUri: Uri
    ) {
        _isLoading.value = true

        // Step 1: Delete the old image
        repository.deleteProductImage(updatedProduct.image_url) { imageDeleted ->
            if (imageDeleted) {
                // Step 2: Upload the new image
                repository.uploadImage(newImageUri) { newImageUrl ->
                    if (newImageUrl != null) {
                        // Step 3: Update the product with the new image URL
                        val updatedProductWithNewImage = when (updatedProduct) {
                            is MakananBeratModel -> MakananBeratModel(
                                productId = updatedProduct.productId,
                                name = updatedProduct.name,
                                description = updatedProduct.description,
                                price = updatedProduct.price,
                                image_url = newImageUrl,
                                stock = updatedProduct.stock
                            )
                            is MakananRinganModel -> MakananRinganModel(
                                productId = updatedProduct.productId,
                                name = updatedProduct.name,
                                description = updatedProduct.description,
                                price = updatedProduct.price,
                                image_url = newImageUrl,
                                stock = updatedProduct.stock
                            )
                            else -> throw IllegalArgumentException("Unknown ProductModel type")
                        }

                        repository.updateProduct(productId, updatedProductWithNewImage, category, clearImage = false) { success ->
                            _isLoading.value = false
                            _productUpdated.value = success
                            if (!success) _errorMessage.value = "Failed to update product with new image"
                        }
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "Failed to upload new image"
                    }
                }
            } else {
                _isLoading.value = false
                _errorMessage.value = "Failed to delete old image"
            }
        }
    }


    fun deleteProduct(productId: String, category: String, imageUrl: String) {
        _isLoading.value = true
        repository.deleteProduct(productId, category, imageUrl) { success ->
            _isLoading.value = false
            _productDeleted.value = success
            if (!success) {
                _errorMessage.value = "Failed to delete product"
            }
        }
    }
}
