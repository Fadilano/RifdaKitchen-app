package com.submission.rifda_kitchen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.CartItemListBinding
import com.submission.rifda_kitchen.model.CartModel

class CartAdapter(
    private val cartList: List<CartModel>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartModel) {
            binding.apply {
                tvProductName.text = cartItem.name
                tvProductPrice.formatPrice(cartItem.price)
                tvProductQuantity.text = cartItem.quantity.toString()
                //change load
                Glide.with(ivProduct.context).load(R.drawable.sample).into(ivProduct)

                btnPlus.setOnClickListener {
                    updateQuantity(cartItem, cartItem.quantity + 1)
                }

                // Handle the minus button click
                btnMinus.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        updateQuantity(cartItem, cartItem.quantity - 1)
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Minimum quantity is 1",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        private fun updateQuantity(cartItem: CartModel, newQuantity: Int) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
            val cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)
                .child(cartItem.name!!)

            cartRef.child("quantity").setValue(newQuantity)
                .addOnSuccessListener {
                    cartItem.quantity = newQuantity
                    binding.tvProductQuantity.text = newQuantity.toString()
                    Toast.makeText(binding.root.context, "Quantity updated", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        binding.root.context,
                        "Failed to update quantity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            CartItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size
}
