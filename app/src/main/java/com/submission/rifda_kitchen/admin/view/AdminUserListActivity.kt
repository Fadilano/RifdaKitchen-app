package com.submission.rifda_kitchen.admin.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.adapter.UserAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdminUserListBinding

class AdminUserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUserListBinding
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(AdminRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setSupportActionBar(binding.Toolbar)
        supportActionBar?.title = ("User List")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        binding.rvUserList.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {

        adminViewModel.users.observe(this) { userList ->
            val adapter = UserAdapter(this, userList) { user, newRole ->
                // Update role in the database
                user.uid?.let { adminViewModel.updateUserRole(it, newRole) }
            }
            binding.rvUserList.adapter = adapter
        }

        // Fetch users from Firebase
        adminViewModel.fetchUsers()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
