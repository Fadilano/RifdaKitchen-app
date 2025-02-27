package com.submission.rifda_kitchen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
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

                Glide.with(ivProduct.context).load(cartItem.image_url).into(ivProduct)

                btnPlus.setOnClickListener {
                    updateQuantity(cartItem, cartItem.quantity + 1)
                }

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
            cartViewmodel?.updateItemQuantity(cartItem, newQuantity)
        }

        private fun removeItem(cartItem: CartModel) {
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
        val diffCallback = CartDiffCallback(cartList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cartList = newList
        diffResult.dispatchUpdatesTo(this)
    }
}

class CartDiffCallback(
    private val oldList: List<CartModel>,
    private val newList: List<CartModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productId == newList[newItemPosition].productId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

