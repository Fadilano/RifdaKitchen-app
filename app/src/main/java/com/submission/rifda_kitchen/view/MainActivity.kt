package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.ActivityMainBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory
import com.submission.rifda_kitchen.viewModel.MainViewmodel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = binding.bottomNavView
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavView.setupWithNavController(navController)

    }


}