package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.adapter.ProductAdapter
import com.submission.rifda_kitchen.databinding.FragmentMakananRinganBinding
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.ProductViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class MakananRinganFragment : Fragment() {

    private lateinit var binding: FragmentMakananRinganBinding
    private val repository = Repository()
    private lateinit var productAdapter: ProductAdapter
    private val productViewmodel: ProductViewmodel by viewModels { ViewmodelFactory(repository) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMakananRinganBinding.inflate(inflater, container, false)
        showProducts()
        productViewmodel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        return binding.root
    }


    private fun showProducts() {
        productAdapter = ProductAdapter(emptyList()) {
                product ->
            // Create an Intent to launch DetailActivity
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("MAKANANRINGAN_EXTRA", product as MakananRinganModel) // Update with actual image resource
            }
            startActivity(intent)
        }
        binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProduct.adapter = productAdapter

        productViewmodel.fetchMakananRingan()

        productViewmodel.makananRinganList.observe(viewLifecycleOwner) { list ->

            productAdapter.updateList(list)

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}