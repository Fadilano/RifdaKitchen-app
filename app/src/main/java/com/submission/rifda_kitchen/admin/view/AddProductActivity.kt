package com.submission.rifda_kitchen.admin.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAddProductBinding



class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private val repository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Tambah Produk"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnAddImage.setOnClickListener {
            startGallery()
        }

        adminViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading, binding.progressBar)
        }

        binding.btnSaveProduct.setOnClickListener {
            val productName = binding.tvProductName.text.toString().trim()
            val productDescription = binding.tvProductDescription.text.toString().trim()
            val productPrice = binding.tvProductPrice.text.toString().toIntOrNull()
            val productStock = binding.tvProductStock.text.toString().toIntOrNull()
            val category = when (binding.RgProductCategory.checkedRadioButtonId) {
                R.id.RbMakananBerat -> "makananberat"
                R.id.RbMakananRingan -> "makananringan"
                else -> null
            }

            if (productName.isEmpty() || productDescription.isEmpty() || productPrice == null ||productStock == null || category == null) {
                Toast.makeText(this, "Please fill all fields and select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentImageUri == null) {
                Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            adminViewModel.saveProductToFirebase(productName, productDescription, productPrice, category, currentImageUri!!, productStock)
        }

        adminViewModel.imageUploadStatus.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
            }
        }

        adminViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivProduct.setImageURI(uri)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) // Correct usage here
    }

}

