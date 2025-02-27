package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.submission.rifda_kitchen.adapter.AdminProductViewPagerAdapter
import com.submission.rifda_kitchen.databinding.ActivityAdminProductListBinding
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class AdminProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProductListBinding
    private val repository = Repository()
    private val userViewmodel: UserViewmodel by viewModels { ViewmodelFactory(repository) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.ProductListToolbar)
        supportActionBar?.title = ("Products")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = AdminProductViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        userViewmodel.getCurrentUser()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Makanan Berat"
                1 -> "Makanan Ringan"
                else -> null
            }
        }.attach()

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        hideAddButton()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun hideAddButton() {
        Log.d("DetailActivity", "Fetching user role...")
        userViewmodel.currentUser.observe(this) { user ->
            Log.d("DetailActivity", "User role: ${user?.role}")
            if (user?.role == "Order Validator") {
                binding.btnAddProduct.visibility = View.GONE
            }
        }

    }
}