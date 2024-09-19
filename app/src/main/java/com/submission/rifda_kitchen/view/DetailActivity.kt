package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityDetailBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory
import com.submission.rifda_kitchen.viewModel.DetailViewmodel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repository = Repository()
    private val detailViewModel : DetailViewmodel by viewModels{ ViewmodelFactory(repository) }

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
    }

    private fun observeViewModel() {
        detailViewModel.makananBeratLiveData.observe(this, Observer { makananBerat ->
            makananBerat?.let { bindProductDetails(it) }
        })

        detailViewModel.makananRinganLiveData.observe(this, Observer { makananRingan ->
            makananRingan?.let { bindProductDetails(it) }
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
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
                    setupAddToCartButton(product)
                }
            }
            is MakananRinganModel -> {
                binding.apply {
                    tvProductName.text = product.name
                    tvProductPrice.formatPrice(product.price)
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
                    setupAddToCartButton(product)
                }
            }
        }
    }

    private fun setupAddToCartButton(product: Any) {
        binding.btnAddToCart.setOnClickListener {
            showQuantityInputDialog(product)
        }
    }

    private fun showQuantityInputDialog(product: Any) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Masukan Jumlah Produk")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val quantityString = input.text.toString()
            val quantity = quantityString.toIntOrNull()

            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Masukan jumlah yang sesuai", Toast.LENGTH_SHORT).show()
            } else {
                detailViewModel.addToCart(product, quantity)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
