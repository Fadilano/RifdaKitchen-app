package com.submission.rifda_kitchen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.Helper.showLoading
import com.submission.rifda_kitchen.adapter.CartAdapter
import com.submission.rifda_kitchen.databinding.FragmentCartBinding
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.CartViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val repository = Repository()
    private val cartViewmodel: CartViewmodel by viewModels { ViewmodelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        showCartItems()
        cartViewmodel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it, binding.progressBar)
        }
        cartViewmodel.getTotalPrice()
        cartViewmodel.totalPrices.observe(viewLifecycleOwner) {
            binding.tvTotal.formatPrice(it)
        }


        return binding.root
    }

    private fun showCartItems() {

        cartAdapter = CartAdapter(emptyList())
        binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProduct.adapter = cartAdapter
        cartViewmodel.fetchCartItems()
        cartViewmodel.cartItemList.observe(viewLifecycleOwner) { list ->
            cartAdapter.updateList(list)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
