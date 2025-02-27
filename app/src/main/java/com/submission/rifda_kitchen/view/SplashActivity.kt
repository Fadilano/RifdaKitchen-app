package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.admin.view.AdminActivity
import com.submission.rifda_kitchen.databinding.ActivitySplashBinding
import com.submission.rifda_kitchen.orderValidator.OrderValidatorActivity
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val repository = Repository()
    private val userViewmodel: UserViewmodel by viewModels { ViewmodelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logo.alpha = 0f
        binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
            checkUserAndRole()
        }
    }

    private fun checkUserAndRole() {
        // Show loading indicator
        showLoading(true, binding.progressBar)

        userViewmodel.getCurrentUser()
        userViewmodel.currentUser.observe(this) { user ->
            showLoading(false, binding.progressBar)

            if (user != null) {
                when (user.role) {
                    "User" -> navigateTo(MainActivity::class.java)
                    "Admin" -> navigateTo(AdminActivity::class.java)
                    "Order Validator" -> navigateTo(OrderValidatorActivity::class.java)
                    else -> navigateTo(LoginActivity::class.java)
                }
            } else {
                navigateTo(LoginActivity::class.java)
            }
        }
    }

    private fun <T> navigateTo(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
        finish()
    }
}
