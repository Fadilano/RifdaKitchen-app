package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityDetailBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.DetailViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repository = Repository()
    private val detailViewModel: DetailViewmodel by viewModels { ViewmodelFactory(repository) }

    private var quantity = 1 // Default quantity
    private var maxStock = 0 // Untuk menyimpan stok maksimum produk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val makananBerat = intent.getParcelableExtra<MakananBeratModel>("MAKANANBERAT_EXTRA")
        val makananRingan = intent.getParcelableExtra<MakananRinganModel>("MAKANANRINGAN_EXTRA")

        if (makananBerat != null) {
            detailViewModel.setMakananBerat(makananBerat)
        } else if (makananRingan != null) {
            detailViewModel.setMakananRingan(makananRingan)
        }

        observeViewModel()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupQuantityButtons() // Atur logika tombol plus dan minus
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun observeViewModel() {
        detailViewModel.makananBeratLiveData.observe(this, Observer { makananBerat ->
            makananBerat?.let {
                maxStock = it.stock
                bindProductDetails(it)
            }
        })

        detailViewModel.makananRinganLiveData.observe(this, Observer { makananRingan ->
            makananRingan?.let {
                maxStock = it.stock
                bindProductDetails(it)
            }
        })

        detailViewModel.addToCartSuccess.observe(this, Observer { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun bindProductDetails(product: Any) {
        when (product) {
            is MakananBeratModel -> {
                binding.apply {
                    tvProductName.text = product.name
                    tvProductPrice.formatPrice(product.price)
                    tvProductStock.text = product.stock.toString()
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(product.image_url).into(ivProduct)
                    supportActionBar?.title = product.name
                }
            }
            is MakananRinganModel -> {
                binding.apply {
                    tvProductName.text = product.name
                    tvProductPrice.formatPrice(product.price)
                    tvProductStock.text = product.stock.toString()
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(product.image_url).into(ivProduct)
                    supportActionBar?.title = product.name
                }
            }
        }
    }

    private fun setupQuantityButtons() {
        binding.apply {
            tvProductQuantity.text = quantity.toString()

            btnPlus.setOnClickListener {
                if (quantity < maxStock) {
                    quantity++
                    tvProductQuantity.text = quantity.toString()
                } else {
                    Toast.makeText(this@DetailActivity, "Stok maksimum tercapai", Toast.LENGTH_SHORT).show()
                }
            }

            btnMinus.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    tvProductQuantity.text = quantity.toString()
                } else {
                    Toast.makeText(this@DetailActivity, "Jumlah minimum adalah 1", Toast.LENGTH_SHORT).show()
                }
            }

            btnAddToCart.setOnClickListener {
                val product = detailViewModel.makananBeratLiveData.value ?: detailViewModel.makananRinganLiveData.value
                product?.let {
                    detailViewModel.addToCart(it, quantity)
                }
            }
        }
    }
}
