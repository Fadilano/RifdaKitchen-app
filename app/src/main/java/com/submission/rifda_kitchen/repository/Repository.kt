package com.submission.rifda_kitchen.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel

class Repository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun fetchMakananBerat(callback: (List<MakananBeratModel>) -> Unit) {
        database.child("products/makananberat").addValueEventListener(object : ValueEventListener {
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
        database.child("products/makananringan").addValueEventListener(object : ValueEventListener {
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
}
