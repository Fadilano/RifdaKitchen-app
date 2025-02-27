package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.admin.view.AdminEditProductActivity
import com.submission.rifda_kitchen.databinding.ActivityDetailBinding
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.DetailViewmodel
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repository = Repository()
    private val detailViewModel: DetailViewmodel by viewModels { ViewmodelFactory(repository) }
    private val userViewmodel: UserViewmodel by viewModels { ViewmodelFactory(repository) }

    private val adminrepository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(adminrepository) }
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
        userViewmodel.getCurrentUser()
        showAdminButton()
        observeViewModel()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigateToEditProduct()
        deleteProductWithConfirmation()


        setupQuantityButtons() // Atur logika tombol plus dan minus
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun deleteProductWithConfirmation() {
        binding.btnDeleteProduct.setOnClickListener {
            val makananBerat = detailViewModel.makananBeratLiveData.value
            val makananRingan = detailViewModel.makananRinganLiveData.value

            // Create a confirmation dialog
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Hapus Produk")
            builder.setMessage("Apakah Anda yakin ingin menghapus produk ini?")
            builder.setPositiveButton("Ya") { _, _ ->
                when {
                    makananBerat != null -> {
                        adminViewModel.deleteProduct(
                            productId = makananBerat.productId,
                            category = "makananberat",
                            imageUrl = makananBerat.image_url
                        )
                    }

                    makananRingan != null -> {
                        adminViewModel.deleteProduct(
                            productId = makananRingan.productId,
                            category = "makananringan",
                            imageUrl = makananRingan.image_url
                        )
                    }

                    else -> {
                        Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }

                // Observe deletion result
                adminViewModel.productDeleted.observe(this) { success ->
                    if (success) {
                        Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    } else {
                        Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog if "No" is clicked
            }

            // Show the dialog
            builder.create().show()
        }
    }


    private fun navigateToEditProduct() {
        binding.btnEditProduct.setOnClickListener {
            val makananBerat = detailViewModel.makananBeratLiveData.value
            val makananRingan = detailViewModel.makananRinganLiveData.value

            val intent = Intent(this, AdminEditProductActivity::class.java).apply {
                if (makananBerat != null) {
                    putExtra("MAKANANBERAT_EXTRA", makananBerat)
                } else if (makananRingan != null) {
                    putExtra("MAKANANRINGAN_EXTRA", makananRingan)
                }
            }
            // Memulai aktivitas untuk mendapatkan hasil
            startActivityForResult(intent, REQUEST_EDIT_PRODUCT)
        }
    }


    private fun showAdminButton() {
        userViewmodel.currentUser.observe(this) { user ->
            if (user?.role == "Admin") {
                binding.btnEditProduct.visibility = View.VISIBLE
                binding.btnDeleteProduct.visibility = View.VISIBLE
                binding.LayoutAddToCart.visibility = View.GONE
                binding.btnAddToCart.visibility = View.GONE
            } else if (user?.role == "Order Validator") {
                binding.btnEditProduct.visibility = View.GONE
                binding.btnDeleteProduct.visibility = View.GONE
                binding.LayoutAddToCart.visibility = View.GONE
                binding.btnAddToCart.visibility = View.GONE
            }
            else {
                binding.btnEditProduct.visibility = View.GONE
                binding.btnDeleteProduct.visibility = View.GONE
            }
        }
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

                    if (product.status == "pre-order") {
                        // Tampilkan status pre-order
                        tvProductStock.text =
                            "Pre-order" // Ganti dengan informasi lebih lanjut tentang pre-order
                    }
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

                    if (product.status == "pre-order") {
                        // Tampilkan status pre-order
                        tvProductStock.text =
                            "Pre-order" // Ganti dengan informasi lebih lanjut tentang pre-order
                    }
                }
            }
        }
    }


    private fun setupQuantityButtons() {
        binding.apply {
            tvProductQuantity.text = quantity.toString()

            btnPlus.setOnClickListener {
                val product = detailViewModel.makananBeratLiveData.value
                    ?: detailViewModel.makananRinganLiveData.value
                product?.let {
                    // Jika produk pre-order dan stok 0, tidak ada batasan jumlah
                    if (it.status == "pre-order" && it.stock == 0) {
                        quantity++ // Tambahkan kuantitas tanpa batasan
                        tvProductQuantity.text = quantity.toString()
                    } else {
                        // Jika produk tidak pre-order, batasi jumlah berdasarkan stok maksimum
                        if (quantity < maxStock) {
                            quantity++
                            tvProductQuantity.text = quantity.toString()
                        } else {
                            Toast.makeText(
                                this@DetailActivity,
                                "Stok maksimum tercapai",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Log.d("DetailActivity", "Status: ${it.status}, Stock: ${it.stock}")
                }

            }

            btnMinus.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    tvProductQuantity.text = quantity.toString()
                } else {
                    Toast.makeText(
                        this@DetailActivity,
                        "Jumlah minimum adalah 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnAddToCart.setOnClickListener {
                val product = detailViewModel.makananBeratLiveData.value
                    ?: detailViewModel.makananRinganLiveData.value
                product?.let {
                    detailViewModel.addToCart(it, quantity) // product sudah mengandung productId
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_PRODUCT && resultCode == RESULT_OK) {
            val updatedMakananBerat = data?.getParcelableExtra<MakananBeratModel>("UPDATED_MAKANANBERAT")
            val updatedMakananRingan = data?.getParcelableExtra<MakananRinganModel>("UPDATED_MAKANANRINGAN")

            if (updatedMakananBerat != null) {
                detailViewModel.setMakananBerat(updatedMakananBerat)
                bindProductDetails(updatedMakananBerat)
            } else if (updatedMakananRingan != null) {
                detailViewModel.setMakananRingan(updatedMakananRingan)
                bindProductDetails(updatedMakananRingan)
            }

            Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }

    companion object {
        private const val REQUEST_EDIT_PRODUCT = 1001
    }
}
