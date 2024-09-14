package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityDetailBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val makananBerat = intent.getParcelableExtra<MakananBeratModel>("MAKANANBERAT_EXTRA")
        val makananRingan = intent.getParcelableExtra<MakananRinganModel>("MAKANANRINGAN_EXTRA")

        if (makananBerat != null) {
            bindProductDetails(makananBerat)
            setupAddToCartButton(makananBerat)
        } else if (makananRingan != null) {
            bindProductDetails(makananRingan)
            setupAddToCartButton(makananRingan)
        }
    }

    private fun bindProductDetails(product: Any) {
        when (product) {
            is MakananBeratModel -> {
                binding.apply {
                    tvProductName.text = product.name
                    tvProductPrice.formatPrice(product.price)
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
                }
            }

            is MakananRinganModel -> {
                binding.apply {
                    tvProductName.text = product.name
                    tvProductPrice.formatPrice(product.price)
                    tvProductDescription.text = product.description
                    Glide.with(this@DetailActivity).load(R.drawable.sample).into(ivProduct)
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
        builder.setTitle("Enter Quantity")

        // Set up the input field
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val quantityString = input.text.toString()
            val quantity = quantityString.toIntOrNull()

            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with adding the product to the cart
                addToCart(product, quantity)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addToCart(product: Any, quantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        // Create cart item based on product type
        val cartItem = when (product) {
            is MakananBeratModel -> CartModel(product.name, product.price, quantity)
            is MakananRinganModel -> CartModel(product.name, product.price, quantity)
            else -> return
        }

        // Check if the item already exists in the cart
        cartRef.child(cartItem.name!!).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // If the item already exists, show a toast message
                Toast.makeText(this, "${cartItem.name} is already in the cart", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // If the item does not exist, add it to the cart
                cartRef.child(cartItem.name!!).setValue(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "${cartItem.name} added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            // Handle any failure in reading from the database
            Toast.makeText(this, "Error checking cart", Toast.LENGTH_SHORT).show()
        }
    }
}
