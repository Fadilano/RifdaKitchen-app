package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.submission.rifda_kitchen.adapter.LatestOrderAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdminBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.view.LoginActivity
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var latestOrderAdapter: LatestOrderAdapter


    private val repository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }
    private val authRepository = AuthRepository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.adminToolbar)
        supportActionBar?.title = ("Admin Dashboard")
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        adminViewModel.fetchTodayOrdersCount()

        // Observe the data and update the UI
        adminViewModel.todayOrdersCount.observe(this) { count ->
            binding.tvCurrentDateOrderCount.text = count.toString()
        }

    
        binding.tvGotoOrderList.setOnClickListener {
            val intent = Intent(this, AdminOrderListActivity::class.java)
            startActivity(intent)
        }

        binding.btnListProduct.setOnClickListener {
            val intent = Intent(this, AdminProductListActivity::class.java)
            startActivity(intent)
        }

        binding.btnListOrder.setOnClickListener {
            val intent = Intent(this, AdminOrderListActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.btnListUser.setOnClickListener {
            val intent = Intent(this, AdminUserListActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

    }


    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar?")

        builder.setPositiveButton("Ya") { _, _ ->
            // Proses logout
            authViewmodel.signOUt(this)
            val intent = Intent(this@AdminActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            // Menutup dialog tanpa melakukan tindakan
            dialog.dismiss()
        }

        builder.create().show()
    }


}
