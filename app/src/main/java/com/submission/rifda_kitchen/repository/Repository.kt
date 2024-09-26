package com.submission.rifda_kitchen.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.model.UserModel

class Repository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private val productRef: DatabaseReference = database.child("products")
    private val cartRef: DatabaseReference? = auth.uid?.let {
        database.child("carts").child(it)
    }
    val orderRef: DatabaseReference = database.child("orders")
    private val userRef: DatabaseReference = database.child("users")

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserUID(): String? {
        return auth.currentUser?.uid
    }

    // Fetch user data from Firebase Realtime Database
    fun fetchUserData(callback: (UserModel?) -> Unit) {
        val uid = getCurrentUserUID()
        if (uid != null) {
            userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }

    fun fetchMakananBerat(callback: (List<MakananBeratModel>) -> Unit) {
        productRef.child("makananberat").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val makananBeratList = mutableListOf<MakananBeratModel>()
                for (snapshot in dataSnapshot.children) {
                    val makananBerat = snapshot.getValue(MakananBeratModel::class.java)
                    makananBerat?.let { makananBeratList.add(it) }
                }
                callback(makananBeratList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                callback(emptyList())
            }
        })
    }

    fun fetchMakananRingan(callback: (List<MakananRinganModel>) -> Unit) {
        productRef.child("makananringan").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val makananRinganList = mutableListOf<MakananRinganModel>()
                for (snapshot in dataSnapshot.children) {
                    val makananRingan = snapshot.getValue(MakananRinganModel::class.java)
                    makananRingan?.let { makananRinganList.add(it) }
                }
                callback(makananRinganList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                callback(emptyList())
            }
        })
    }

    fun fetchCartItem(callback: (List<CartModel>) -> Unit) {
        cartRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartModel>()
                for (snapshot in dataSnapshot.children) {
                    val cartItem = snapshot.getValue(CartModel::class.java)
                    cartItem?.let { cartItems.add(it) }
                }
                callback(cartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getTotalPrice(callback: (Int) -> Unit) {
        fetchCartItem { cartItems ->
            var totalPrice = 0
            for (cartItem in cartItems) {
                totalPrice += cartItem.price * cartItem.quantity
            }
            callback(totalPrice)
        }
    }

    fun addToCart(product: Any, quantity: Int, callback: (String?) -> Unit) {
        val cartItem = when (product) {
            is MakananBeratModel -> CartModel(
                name = product.name,
                price = product.price,
                quantity = quantity
            )

            is MakananRinganModel -> CartModel(
                name = product.name,
                price = product.price,
                quantity = quantity
            )

            else -> return
        }

        // Check if the cart item already exists in the database
        cartRef?.child(cartItem.name!!)?.get()?.addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // If the item already exists, notify the user
                callback("${cartItem.name} sudah ada di keranjang")
            } else {
                // If the item does not exist, add it to the cart with only necessary fields
                val cartData = mapOf(
                    "name" to cartItem.name,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity
                )
                cartRef.child(cartItem.name!!).setValue(cartData).addOnSuccessListener {
                    callback("${cartItem.name} berhasil ditambahkan ke keranjang")
                }.addOnFailureListener {
                    callback("produk gagal ditambahkan ke keranjang")
                }
            }
        }?.addOnFailureListener {
            callback("Error checking cart")
        }
    }


    fun updateItemQuantity(
        cartItem: CartModel,
        quantity: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        cartRef?.child(cartItem.name!!)?.child("quantity")?.setValue(quantity)
            ?.addOnSuccessListener {
                cartItem.quantity = quantity
                callback(true, "Jumlah telah diperbaharui")
            }?.addOnFailureListener {
                callback(false, "Jumlah gagal diperbaharui")
            }
    }

    fun removeItem(cartItem: CartModel, callback: (Boolean, String) -> Unit) {
        cartRef?.child(cartItem.name!!)?.removeValue()?.addOnSuccessListener {
            callback(true, "Item removed from cart")
        }?.addOnFailureListener {
            callback(false, "Failed to remove item")
        }
    }
    fun removeAllItems(callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false, "User not logged in")
        cartRef?.removeValue()
            ?.addOnSuccessListener { callback(true, "All items removed") }
            ?.addOnFailureListener { e -> callback(false, "Failed to remove items: ${e.message}") }
    }



    fun saveOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            val orderId = orderRef.child(userId).push().key // Generate a unique order ID
            if (orderId != null) {
                val orderToSave = order.copy(orderId = orderId, userId = userId) // Set orderId and userId

                orderRef.child(userId).child(orderId).setValue(orderToSave)
                    .addOnSuccessListener {
                        callback(true, "Order berhasil dibuat, silahkan tunggu konfirmasi dari admin!")
                    }
                    .addOnFailureListener {
                        callback(false, "Gagal membuat order")
                    }
            } else {
                callback(false, "Login terlebih dahulu")
            }
        } else {
            callback(false, "Pengguna tidak terautentikasi")
        }
    }


    fun fetchOrdersForUser(userUID: String, callback: (List<OrderModel>?) -> Unit) {
        val userOrdersRef = orderRef.child(userUID)

        userOrdersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ordersList = mutableListOf<OrderModel>()
                for (orderSnapshot in dataSnapshot.children) {
                    val orderDetail = orderSnapshot.getValue(OrderModel::class.java)
                    orderDetail?.let {
                        ordersList.add(it)
                    }
                }
                callback(ordersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                callback(null)
            }
        })
    }

    suspend fun updateUserDetails(userId: String, phone: String, address: String) {
        userRef.child(userId).child("phone").setValue(phone)
        userRef.child(userId).child("address").setValue(address)
    }

}





