package com.submission.rifda_kitchen.admin.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.submission.rifda_kitchen.adapter.AdminOrderViewPagerAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdmiinOrderListBinding
import com.submission.rifda_kitchen.model.OrderModel

class AdminOrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmiinOrderListBinding
    private val repository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }

    // Initialize ViewModel later
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdmiinOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.OrderListToolbar)
        supportActionBar?.title = ("Order List")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = AdminOrderViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Menunggu Konfirmasi"
                1 -> "Menunggu Pembayaran"
                2 -> "Pesanan Diproses"
                3 -> "Pesanan Selesai"
                else -> null
            }
        }.attach()


    }

    override fun onSupportNavigateUp(): Boolean {
        // Memanggil onBackPressed untuk menavigasi kembali dan me-refresh data
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Menyegarkan data setelah kembali
        refreshOrderList()
    }

    private fun refreshOrderList() {
        // Dapatkan fragment yang sedang aktif dan lakukan refresh data
        val fragment = supportFragmentManager.findFragmentById(binding.viewPager.id)
        if (fragment is AdminOrderListFragment) {
            fragment.refreshRecyclerView()
        }
    }
}
