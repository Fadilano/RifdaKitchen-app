package com.submission.rifda_kitchen.orderValidator

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.admin.view.AdminOrderListActivity
import com.submission.rifda_kitchen.admin.view.AdminProductListActivity
import com.submission.rifda_kitchen.databinding.ActivityOrderValidatorBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.view.LoginActivity
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory

class OrderValidatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderValidatorBinding
    private val repository = AdminRepository()
    private val authRepository = AuthRepository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderValidatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.OrvaToolbar)
        supportActionBar?.title = ("Validator Dashboard")
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        adminViewModel.fetchTodayOrdersCount()
        adminViewModel.todayOrdersCount.observe(this) { count ->
            binding.tvCurrentDateOrderCount.text = count.toString()
        }

        binding.btnListOrder.setOnClickListener {
            val intent = Intent(this, AdminOrderListActivity::class.java)
            startActivity(intent)
        }
        binding.tvGotoOrderList.setOnClickListener {
            val intent = Intent(this, AdminOrderListActivity::class.java)
            startActivity(intent)
        }

        binding.btnListProduct.setOnClickListener {
            val intent = Intent(this, AdminProductListActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

    }


    private fun signOut() {
        // Membuat dialog konfirmasi
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar?")

        // Tombol "Ya"
        builder.setPositiveButton("Ya") { _, _ ->
            // Proses logout
            authViewmodel.signOUt(this)
            val intent = Intent(this@OrderValidatorActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Tombol "Tidak"
        builder.setNegativeButton("Tidak") { dialog, _ ->
            // Menutup dialog tanpa melakukan tindakan
            dialog.dismiss()
        }

        // Menampilkan dialog
        builder.create().show()
    }


}