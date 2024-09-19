package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.CartAdapter
import com.submission.rifda_kitchen.databinding.FragmentCartBinding
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.CartViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter
    private val cartViewModel: CartViewmodel by viewModels { ViewmodelFactory(Repository()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Fetch the cart items when the fragment is created
        cartViewModel.fetchCartItems()
        cartViewModel.getTotalPrice()

        binding.btnCheckout.setOnClickListener {
            navigateToOrderActivity()
        }
    }

    private fun setupRecyclerView() {
        // Set up the RecyclerView with the CartAdapter and pass the ViewModel
        cartAdapter = CartAdapter(emptyList(), cartViewModel)

        binding.rvProduct.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        cartViewModel.cartItemList.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems != null) {
                if (cartItems.isNotEmpty()) {
                    cartAdapter.updateList(cartItems)
                } else {
                    Log.d("CartViewModel", "No items in cart")
                }
            }
        }

        cartViewModel.totalPrices.observe(viewLifecycleOwner) { totalPrice ->
            binding.tvTotal.formatPrice(totalPrice)
        }

        cartViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        cartViewModel.quantityUpdateMessage.observe(viewLifecycleOwner) { message ->
            Log.d("CartViewModel", "Quantity update message: $message")

        }
    }

    private fun navigateToOrderActivity() {
        cartViewModel.cartItemList.value?.let { cartItems ->
            cartViewModel.totalPrices.value?.let { totalPrice ->
                val intent = Intent(requireContext(), OrderActivity::class.java).apply {
                    putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
                    putExtra("totalPrice", totalPrice)
                }
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
