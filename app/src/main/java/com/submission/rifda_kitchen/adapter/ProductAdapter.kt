package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ProductItemListBinding
import com.submission.rifda_kitchen.model.ProductModel

class ProductAdapter(
    private var productList: List<ProductModel>,
    private val onItemClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    inner class ProductViewHolder(private val binding: ProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            binding.apply {
                tvProductName.text = product.name
                tvProductPrice.formatPrice(product.price)
                tvProductStock.text = product.stock.toString()
                if (product.stock > 0) {
                    tvProductStock.visibility = View.VISIBLE
                    tvProductStatus.visibility = View.GONE
                } else {
                    tvProductStock.visibility = View.GONE
                    tvProductStatus.visibility = View.VISIBLE
                    tvProductStatus.text = "Pre order"
                }
                Glide.with(ivProduct.context)
                    .load(product.image_url)
                    .into(ivProduct)
                ivProduct.setOnClickListener {
                    onItemClick(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ProductItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<ProductModel>) {
        productList = newList
        notifyDataSetChanged()
    }

}
