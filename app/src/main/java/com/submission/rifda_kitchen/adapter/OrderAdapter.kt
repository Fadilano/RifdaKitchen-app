package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.submission.rifda_kitchen.databinding.OrderItemListBinding
import com.submission.rifda_kitchen.model.CartModel

class OrderAdapter(private val cartItems: List<CartModel>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            OrderItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class OrderViewHolder(private val binding: OrderItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartModel) {
            binding.tvProductName.text = cartItem.name
            binding.tvProductQuantity.text = cartItem.quantity.toString()

        }
    }
}
