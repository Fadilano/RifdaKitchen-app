package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.adapter.ProductAdapter
import com.submission.rifda_kitchen.databinding.FragmentMakananBeratBinding
import com.submission.rifda_kitchen.databinding.FragmentMakananRinganBinding
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.ProductViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class MakananRinganFragment : Fragment() {

    private var _binding: FragmentMakananRinganBinding? = null
    private val binding get() = _binding!!
    private val repository = Repository()
    private lateinit var productAdapter: ProductAdapter
    private val productViewmodel: ProductViewmodel by viewModels { ViewmodelFactory(repository) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakananRinganBinding.inflate(inflater, container, false)

        showProducts()

        productViewmodel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it, binding.progressBar)
        }

        return binding.root
    }


    private fun showProducts() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra(
                    "MAKANANRINGAN_EXTRA",
                    product as MakananRinganModel
                )
            }
            startActivity(intent)

        }
        binding.rvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProduct.adapter = productAdapter
        productViewmodel.fetchMakananRingan()
        productViewmodel.makananRinganList.observe(viewLifecycleOwner) { list ->

            productAdapter.updateList(list)

        }
    }


}