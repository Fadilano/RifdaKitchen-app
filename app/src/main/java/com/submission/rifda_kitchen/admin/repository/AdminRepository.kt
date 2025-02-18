package com.submission.rifda_kitchen.admin.repository

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.model.PaymentLinkResponse
import com.submission.rifda_kitchen.admin.retrofit.ApiClient
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.OrderModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.UUID

class AdminRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Fetch the list of orders from Firebase
    fun getAllOrders(): LiveData<List<OrderModel>> {
        val ordersLiveData = MutableLiveData<List<OrderModel>>()
        val orderList = mutableListOf<OrderModel>()

        database.child("orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    for (orderDetailSnapshot in orderSnapshot.children) {
                        val order = orderDetailSnapshot.getValue(OrderModel::class.java)
                        order?.let { orderList.add(it) }
                    }
                }
                ordersLiveData.value = orderList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })

        return ordersLiveData
    }

    fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val storageRef = storage.reference.child("products/${UUID.randomUUID()}")
        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                onComplete(imageUrl.toString())
            }.addOnFailureListener {
                onComplete(null)
            }
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    fun saveProductToDatabase(product: Any, category: String, onComplete: (Boolean) -> Unit) {
        val productRef = database.child("products").child(category).child((product as MakananBeratModel).name.lowercase(
            Locale.getDefault()))
        productRef.setValue(product).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }



    fun updateOrderStatus(userId: String, orderId: String, status: String, clearPaymentLink: Boolean, callback: (Boolean) -> Unit) {
        val updateData = mutableMapOf<String, Any>(
            "orderStatus" to status
        )
        if (clearPaymentLink) {
            updateData["paymentLink"] = ""
        }
        val orderRef = database.child("orders").child(userId).child(orderId)
        orderRef.updateChildren(updateData).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    fun updatePaymentLink(userId: String, orderId: String, paymentLink: String, onComplete: (Boolean) -> Unit) {
        val orderRef = database.child("orders").child(userId).child(orderId)
        orderRef.child("paymentLink").setValue(paymentLink).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createPaymentLink(
        paymentRequest: PaymentLinkRequest,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val ApiService = ApiClient.getMidtransService()
        ApiService.createPaymentLink(paymentRequest).enqueue(object :
            Callback<PaymentLinkResponse> {
            override fun onResponse(call: Call<PaymentLinkResponse>, response: Response<PaymentLinkResponse>) {
                if (response.isSuccessful) {
                    response.body()?.payment_url?.let { onSuccess(it) }
                } else {
                    onFailure(Exception(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<PaymentLinkResponse>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}