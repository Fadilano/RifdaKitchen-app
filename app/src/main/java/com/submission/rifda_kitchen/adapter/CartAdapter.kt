package com.submission.rifda_kitchen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.CartItemListBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.viewModel.CartViewmodel

class CartAdapter(
    private var cartList: List<CartModel>,
    private val cartViewmodel: CartViewmodel?
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
                        removeItem(cartItem)
                    }
                }
            }
        }

        private fun updateQuantity(cartItem: CartModel, newQuantity: Int) {
            // Call the view model to update the quantity
            cartViewmodel?.updateItemQuantity(cartItem, newQuantity)
        }

        private fun removeItem(cartItem: CartModel) {
            // Remove item through ViewModel
            cartViewmodel?.removeItem(cartItem)
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

    fun updateList(newList: List<CartModel>) {
        cartList = newList
        notifyDataSetChanged()
    }
}
