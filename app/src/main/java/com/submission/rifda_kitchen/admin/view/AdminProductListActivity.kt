package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.submission.rifda_kitchen.adapter.AdminViewPagerAdapter
import com.submission.rifda_kitchen.databinding.ActivityAdminProductListBinding

class AdminProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProductListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.ProductListToolbar)
        supportActionBar?.title = ("Products")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = AdminViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

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
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}