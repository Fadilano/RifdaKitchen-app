package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityMainBinding
import com.submission.rifda_kitchen.viewModel.MainViewmodel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewmodel: MainViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewmodel.isUserLoggedIn.observe(this) { loggedIn ->
            if (!loggedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        val bottomNavView = binding.bottomNavView
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavView.setupWithNavController(navController)

    }


}