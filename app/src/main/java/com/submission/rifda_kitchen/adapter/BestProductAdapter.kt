package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.BestProductListBinding
import com.submission.rifda_kitchen.model.ProductModel

class BestProductAdapter(
    private var productList: List<ProductModel>,
    private val onItemClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<BestProductAdapter.ProductViewHolder>() {


    inner class ProductViewHolder(private val binding: BestProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            binding.apply {
                tvProductName.text = product.name
                tvProductPrice.formatPrice(product.price)
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
            BestProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return if (productList.size > 2) 2 else productList.size
    }

    fun updateList(newList: List<ProductModel>) {
        productList = newList
        notifyDataSetChanged()
    }

}
