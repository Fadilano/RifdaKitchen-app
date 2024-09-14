package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.CartAdapter
import com.submission.rifda_kitchen.databinding.FragmentCartBinding
import com.submission.rifda_kitchen.model.CartModel

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        setupCartRecyclerView()
        return binding.root
    }

    private fun setupCartRecyclerView() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return  // Ensure the fragment is still attached to the activity

                val cartItems = mutableListOf<CartModel>()
                var totalPrice = 0

                for (cartSnapshot in snapshot.children) {
                    val cartItem = cartSnapshot.getValue(CartModel::class.java)
                    cartItem?.let {
                        cartItems.add(it)
                        totalPrice += (it.price * it.quantity)  // Calculate total price
                    }
                }

                cartAdapter = CartAdapter(cartItems)
                binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
                binding.rvProduct.adapter = cartAdapter

                // Update the total price on tvTotal
                binding.tvTotal.formatPrice(totalPrice) // Ensure to format the price
            }

            override fun onCancelled(error: DatabaseError) {
                if (!isAdded) return  // Ensure the fragment is still attached before showing a Toast
                Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}
