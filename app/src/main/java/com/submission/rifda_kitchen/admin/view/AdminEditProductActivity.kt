package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.databinding.ActivityAdminEditProductBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository

class AdminEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminEditProductBinding
    private val repository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }

    private var currentImageUri: Uri? = null
    private var oldImageUrl: String? = null
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val makananBerat = intent.getParcelableExtra<MakananBeratModel>("MAKANANBERAT_EXTRA")
        val makananRingan = intent.getParcelableExtra<MakananRinganModel>("MAKANANRINGAN_EXTRA")

        if (makananBerat != null) {
            bindProductDetails(makananBerat)
            category = "makananberat"
        } else if (makananRingan != null) {
            bindProductDetails(makananRingan)
            category = "makananringan"
        }

        binding.btnChangeImage.setOnClickListener {
            startGallery()
        }


        adminViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        binding.btnSaveProduct.setOnClickListener {
            val productName = binding.tvProductName.text.toString().trim()
            val productDescription = binding.tvProductDescription.text.toString().trim()
            val productPrice = binding.tvProductPrice.text.toString().toIntOrNull() ?: 0
            val productStock = binding.tvProductStock.text.toString().toIntOrNull() ?: 0

            if (productName.isEmpty() || productDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (oldImageUrl == null) {
                Toast.makeText(this, "Product data error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val productToUpdate = when (category) {
                "makananberat" -> MakananBeratModel(
                    productId = makananBerat?.productId ?: "",
                    name = productName,
                    description = productDescription,
                    price = productPrice,
                    image_url = oldImageUrl ?: "",
                    stock = productStock
                )
                else -> MakananRinganModel(
                    productId = makananRingan?.productId ?: "",
                    name = productName,
                    description = productDescription,
                    price = productPrice,
                    image_url = oldImageUrl ?: "",
                    stock = productStock
                )
            }

            if (currentImageUri != null) {
                adminViewModel.updateProductWithImage(
                    productToUpdate.productId,
                    productToUpdate,
                    category,
                    currentImageUri!!
                )
            } else {
                adminViewModel.updateProduct(
                    productToUpdate.productId,
                    productToUpdate,
                    category,
                    clearImage = false
                )
            }

            adminViewModel.productUpdated.observe(this) { success ->
                if (success) {
                    val resultIntent = Intent().apply {
                        if (category == "makananberat") {
                            putExtra("UPDATED_MAKANANBERAT", MakananBeratModel(
                                productId = makananBerat?.productId ?: "",
                                name = binding.tvProductName.text.toString().trim(),
                                description = binding.tvProductDescription.text.toString().trim(),
                                price = binding.tvProductPrice.text.toString().toInt(),
                                image_url = currentImageUri?.toString() ?: oldImageUrl.orEmpty(),
                                stock = binding.tvProductStock.text.toString().toInt()
                            ))
                        } else {
                            putExtra("UPDATED_MAKANANRINGAN", MakananRinganModel(
                                productId = makananRingan?.productId ?: "",
                                name = binding.tvProductName.text.toString().trim(),
                                description = binding.tvProductDescription.text.toString().trim(),
                                price = binding.tvProductPrice.text.toString().toInt(),
                                image_url = currentImageUri?.toString() ?: oldImageUrl.orEmpty(),
                                stock = binding.tvProductStock.text.toString().toInt()
                            ))
                        }
                    }
                    setResult(RESULT_OK, resultIntent)
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
                }
            }

        }

        adminViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }


    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivProduct.setImageURI(uri) // Show the selected image
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun bindProductDetails(product: Any) {
        when (product) {
            is MakananBeratModel -> {
                binding.apply {
                    oldImageUrl = product.image_url // Save the old image URL
                    Glide.with(this@AdminEditProductActivity).load(product.image_url).into(ivProduct)
                    tvProductName.setText(product.name)
                    tvProductPrice.setText(product.price.toString())
                    tvProductStock.setText(product.stock.toString())
                    tvProductDescription.setText(product.description)
                    RbMakananBerat.isChecked = true
                }
            }

            is MakananRinganModel -> {
                binding.apply {
                    oldImageUrl = product.image_url // Save the old image URL
                    Glide.with(this@AdminEditProductActivity).load(product.image_url).into(ivProduct)
                    tvProductName.setText(product.name)
                    tvProductPrice.setText(product.price.toString())
                    tvProductStock.setText(product.stock.toString())
                    tvProductDescription.setText(product.description)
                    RbMakananRingan.isChecked = true
                }
            }
        }
    }
}
