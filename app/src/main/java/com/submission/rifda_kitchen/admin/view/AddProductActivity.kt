package com.submission.rifda_kitchen.admin.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityAddProductBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import java.util.*

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ("Tambah Produk")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnAddImage.setOnClickListener {
            startGallery()
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProductToFirebase()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivProduct.setImageURI(it)
        }
    }

    private fun saveProductToFirebase() {
        val productName = binding.tvProductName.text.toString().trim()
        val productDescription = binding.tvProductDescription.text.toString().trim()
        val productPrice = binding.tvProductPrice.text.toString().toIntOrNull()
        val category = when (binding.RgProductCategory.checkedRadioButtonId) {
            R.id.RbMakananBerat -> "makananberat"
            R.id.RbMakananRingan -> "makananringan"
            else -> null
        }

        if (productName.isEmpty() || productDescription.isEmpty() || productPrice == null || category == null) {
            Toast.makeText(this, "Please fill all fields and select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentImageUri == null) {
            Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageToFirebase(productName, productDescription, productPrice, category)
    }

    private fun uploadImageToFirebase(name: String, description: String, price: Int, category: String) {
        val storageRef = storage.reference.child("products/${UUID.randomUUID()}")
        currentImageUri?.let { uri ->
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    saveProductToDatabase(name, description, price, category, imageUrl.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProductToDatabase(name: String, description: String, price: Int, category: String, imageUrl: String) {
        val productRef = database.child("products").child(category).child(name.lowercase(Locale.getDefault()))

        val product = if (category == "makananberat") {
            MakananBeratModel(name, description, price, imageUrl)
        } else {
            MakananRinganModel(name, description, price, imageUrl)
        }

        productRef.setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
