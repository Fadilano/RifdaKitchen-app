package com.submission.rifda_kitchen.admin.repository

import android.net.Uri
import android.os.Build
import android.util.Log
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
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.model.ProductModel
import com.submission.rifda_kitchen.model.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun updateUserRole(userId: String, newRole: String, onComplete: (Boolean) -> Unit) {
        val userRef = database.child("users").child(userId)

        val updates = mapOf("role" to newRole)
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun updateProductStock(productId: String, category: String, newStock: Int, callback: (Boolean) -> Unit) {
        val productRef = database.child("products").child(category).child(productId)
        productRef.child("stock").setValue(newStock).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    fun deleteOrderWithStockUpdate(
        userId: String,
        orderId: String,
        cartItems: List<CartModel>,
        callback: (Boolean) -> Unit
    ) {
        // Validasi input
        if (userId.isEmpty() || orderId.isEmpty()) {
            callback(false)
            Log.e("deleteOrderWithStockUpdate", "userId atau orderId kosong!")
            return
        }

        val orderRef = database.child("orders").child(userId).child(orderId)
        val productCategoryRef = database.child("products")

        var success = true
        var processedCount = 0

        for (cartItem in cartItems) {
            val productId = cartItem.productId ?: continue

            // Cek kategori produk: makanan berat atau ringan
            productCategoryRef.child("makananberat").child(productId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val status = snapshot.child("status").getValue(String::class.java)
                        if (status == "available") {
                            val currentStock = snapshot.child("stock").getValue(Int::class.java) ?: 0
                            val newStock = currentStock + cartItem.quantity
                            productCategoryRef.child("makananberat").child(productId).child("stock")
                                .setValue(newStock)
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) success = false
                                    checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                                }
                        } else {
                            // Produk dengan status pre-order tidak memperbarui stok
                            checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                        }
                    } else {
                        productCategoryRef.child("makananringan").child(productId).get()
                            .addOnSuccessListener { ringanSnapshot ->
                                if (ringanSnapshot.exists()) {
                                    val status = ringanSnapshot.child("status").getValue(String::class.java)
                                    if (status == "available") {
                                        val currentStock = ringanSnapshot.child("stock").getValue(Int::class.java) ?: 0
                                        val newStock = currentStock + cartItem.quantity
                                        productCategoryRef.child("makananringan").child(productId).child("stock")
                                            .setValue(newStock)
                                            .addOnCompleteListener { task ->
                                                if (!task.isSuccessful) success = false
                                                checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                                            }
                                    } else {
                                        // Produk dengan status pre-order tidak memperbarui stok
                                        checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                                    }
                                } else {
                                    success = false
                                    checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                                }
                            }.addOnFailureListener {
                                success = false
                                checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                            }
                    }
                }.addOnFailureListener {
                    success = false
                    checkAndDeleteOrder(++processedCount, cartItems.size, success, orderRef, callback)
                }
        }

        // Jika tidak ada item di keranjang, langsung hapus order
        if (cartItems.isEmpty()) {
            orderRef.removeValue().addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        }
    }

    private fun checkAndDeleteOrder(
        processedCount: Int,
        totalItems: Int,
        success: Boolean,
        orderRef: DatabaseReference,
        callback: (Boolean) -> Unit
    ) {
        if (processedCount == totalItems) {
            orderRef.removeValue().addOnCompleteListener { task ->
                callback(task.isSuccessful && success)
            }
        }
    }

    fun getTodayOrdersCount(callback: (Int) -> Unit) {
        val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val orderRef = database.child("orders")

        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val orderData = orderSnapshot.getValue(OrderModel::class.java)
                        if (orderData?.date == today) {
                            count++
                        }
                    }
                }
                callback(count)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminRepository", "Failed to fetch today's orders: ${error.message}")
                callback(0)
            }
        })
    }





    fun getAllUsers(): LiveData<List<UserModel>> {
        val usersLiveData = MutableLiveData<List<UserModel>>()
        val userList = mutableListOf<UserModel>()

        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserModel::class.java)
                    user?.let { userList.add(it) }
                }
                usersLiveData.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch users: ${error.message}")
            }
        })

        return usersLiveData
    }



    fun getOrdersPerMonth(): LiveData<Map<String, Int>> {
        val monthlyOrdersLiveData = MutableLiveData<Map<String, Int>>()
        val monthMap = mutableMapOf(
            "Jan" to 0, "Feb" to 0, "Mar" to 0, "Apr" to 0,
            "May" to 0, "Jun" to 0, "Jul" to 0, "Aug" to 0,
            "Sep" to 0, "Oct" to 0, "Nov" to 0, "Dec" to 0
        )

        database.child("orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val orderData = orderSnapshot.value as? Map<String, Any>
                        val date = orderData?.get("date") as? String ?: continue

                        // Extract month from date (assuming DD/MM/YYYY format)
                        val monthIndex = date.substring(3, 5).toInt()
                        val monthName = monthMap.keys.toList()[monthIndex - 1]
                        monthMap[monthName] = monthMap[monthName]!! + 1
                    }
                }
                monthlyOrdersLiveData.value = monthMap
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch orders: ${error.message}")
            }
        })

        return monthlyOrdersLiveData
    }




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
        val fileName = UUID.randomUUID().toString() // Generate a unique file name
        val storageRef = storage.reference.child("products/$fileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    Log.d("UploadImage", "Image uploaded successfully: $imageUrl")
                    onComplete(imageUrl.toString())
                }.addOnFailureListener { exception ->
                    Log.e("UploadImage", "Failed to get download URL: ${exception.message}")
                    onComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UploadImage", "Failed to upload image: ${exception.message}")
                onComplete(null)
            }
    }


    fun saveProductToDatabase(
        product: ProductModel,
        category: String,
        callback: (Boolean, String) -> Unit
    ) {
        val productId =
            database.child("products").child(category).push().key // Generate a unique product ID
        if (productId != null) {
            val productToSave = when (product) {
                is MakananBeratModel -> product.copy(productId = productId)
                is MakananRinganModel -> product.copy(productId = productId)
                else -> product // Handle other product types if any
            }
            val productRef = database.child("products").child(category).child(productId)
            productRef.setValue(productToSave)
                .addOnSuccessListener {
                    callback(true, "Produk berhasil disimpan")
                }
                .addOnFailureListener {
                    callback(false, "Gagal menyimpan produk")
                }
        } else {
            callback(false, "Gagal membuat ID produk")
        }
    }


    fun updateOrderStatus(
        userId: String,
        orderId: String,
        status: String,
        clearPaymentLink: Boolean,
        callback: (Boolean) -> Unit
    ) {
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

    fun deleteProduct(
        productId: String,
        category: String,
        imageUrl: String,
        callback: (Boolean) -> Unit
    ) {
        // Step 1: Delete the file from Firebase Storage
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("DeleteProduct", "Image deleted successfully.")

                // Step 2: Delete the product record from Firebase Realtime Database
                val productRef = database.child("products").child(category).child(productId)
                productRef.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("DeleteProduct", "Product deleted successfully.")
                        callback(true)
                    } else {
                        Log.e("DeleteProduct", "Failed to delete product record.")
                        callback(false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteProduct", "Failed to delete image: ${exception.message}")
                callback(false)
            }
    }


    fun updateProduct(
        productId: String,
        updatedProduct: ProductModel,
        category: String,
        clearImage: Boolean = false,  // Optional parameter to clear the image URL
        callback: (Boolean) -> Unit
    ) {
        // Prepare the updated data for the product
        val updateData = mutableMapOf<String, Any>(
            "name" to updatedProduct.name,
            "description" to updatedProduct.description,
            "price" to updatedProduct.price,
            "stock" to updatedProduct.stock,
            "status" to updatedProduct.status,
            "image_url" to if (clearImage) "" else updatedProduct.image_url // Set image_url properly
        )

        // Reference to the product in the database
        val productRef = database.child("products").child(category).child(productId)

        // Update the product data in the Firebase Realtime Database
        productRef.updateChildren(updateData).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }


    fun deleteProductImage(imageUrl: String, onComplete: (Boolean) -> Unit) {
        Log.d("DeleteImage", "Attempting to delete image at URL: $imageUrl")
        try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storageRef.delete()
                .addOnSuccessListener {
                    Log.d("DeleteImage", "Image successfully deleted.")
                    onComplete(true)
                }
                .addOnFailureListener { exception ->
                    if (exception is com.google.firebase.storage.StorageException &&
                        exception.errorCode == com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND
                    ) {
                        Log.w("DeleteImage", "File not found, treating as deleted.")
                        onComplete(true)
                    } else {
                        Log.e("DeleteImage", "Failed to delete image: ${exception.message}")
                        onComplete(false)
                    }
                }
        } catch (e: IllegalArgumentException) {
            Log.e("DeleteImage", "Invalid image URL: ${e.message}")
            onComplete(false)
        }
    }


    fun updatePaymentLink(
        userId: String,
        orderId: String,
        paymentLink: String,
        onComplete: (Boolean) -> Unit
    ) {
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
            override fun onResponse(
                call: Call<PaymentLinkResponse>,
                response: Response<PaymentLinkResponse>
            ) {
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


    fun getLatestOrders(limit: Int): LiveData<List<OrderModel>> {
        val latestOrdersLiveData = MutableLiveData<List<OrderModel>>()
        val orderList = mutableListOf<OrderModel>()

        database.child("orders")
            .limitToLast(limit)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        for (orderSnapshot in userSnapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                    }
                    val sortedOrders = orderList.sortedByDescending { it.orderId }
                    latestOrdersLiveData.value = sortedOrders
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to fetch latest orders: ${error.message}")
                }
            })

        return latestOrdersLiveData
    }


}