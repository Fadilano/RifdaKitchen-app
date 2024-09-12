package com.submission.rifda_kitchen.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityDetailBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.model.ProductModel


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val makananBerat = intent.getParcelableExtra<MakananBeratModel>("MAKANANBERAT_EXTRA")
        val makananRingan = intent.getParcelableExtra<MakananRinganModel>("MAKANANRINGAN_EXTRA")

        if (makananBerat != null) {
            // Handle MakananBeratModel
            makananBerat.let {
                binding.apply {
                    tvProductName.text = it.name
                    tvProductPrice.formatPrice(it.price)
                    tvProductDescription.text = it.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
                }
            }

        } else if (makananRingan != null) {
            // Handle MakananRinganModel
            makananRingan.let {
                binding.apply {
                    tvProductName.text = it.name
                    tvProductPrice.formatPrice(it.price)
                    tvProductDescription.text = it.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
                }
            }

        }
    }
}