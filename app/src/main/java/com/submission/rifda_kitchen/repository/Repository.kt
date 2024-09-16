package com.submission.rifda_kitchen.repository

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

class Repository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private val productRef: DatabaseReference = database.child("products")
    private val cartRef: DatabaseReference? = auth.uid?.let {
        database.child("carts").child(it)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
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
            is MakananBeratModel -> CartModel(product.name, product.price, quantity)
            is MakananRinganModel -> CartModel(product.name, product.price, quantity)
            else -> return
        }

        cartRef?.child(cartItem.name!!)?.get()?.addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // If the item already exists
                callback("${cartItem.name} Produk sudah ada di keranjang")
            } else {
                // If the item does not exist, add it to the cart
                cartRef.child(cartItem.name!!).setValue(cartItem)
                    .addOnSuccessListener {
                        callback("${cartItem.name} berhasil ditambahkan ke keranjang")
                    }
                    .addOnFailureListener {
                        callback("produk gagal ditambahkan ke keranjang")
                    }
            }
        }?.addOnFailureListener {
            callback("Error checking cart")
        }
    }
}





