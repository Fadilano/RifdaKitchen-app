package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.FragmentAdminOrderListBinding
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository


class AdminOrderListFragment : Fragment() {

    private var _binding: FragmentAdminOrderListBinding? = null
    private val binding get() = _binding!!

    private val repository = Repository()
    private val adminrepository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(adminrepository) }

    private var statusFilter: String? = null
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusFilter = it.getString("statusFilter")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        setupRecyclerView()

        adminViewModel.orderStatusUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                // Refresh the orders if the status was updated
                observeOrders()
            }
        }
        // Fetch orders
        observeOrders()
    }

    private fun observeOrders() {
        adminViewModel.getAllOrders().observe(viewLifecycleOwner) { orders ->
            val filteredOrders = if (statusFilter.isNullOrEmpty()) {
                orders // Tanpa filter, tampilkan semua pesanan
            } else {
                orders.filter { it.orderStatus == statusFilter }
            }

            // Urutkan berdasarkan date dan time terbaru
            val sortedOrders = filteredOrders.sortedWith(
                compareByDescending<OrderModel> { it.date }
                    .thenByDescending { it.time }
            )

            // Update UI
            if (sortedOrders.isEmpty()) {
                binding.tvNoItem.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvNoItem.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                (binding.rvHistory.adapter as HistoryAdapter).updateList(sortedOrders)
            }

            // Sembunyikan progress bar
            binding.progressBar.visibility = View.GONE
        }
    }



    private fun setupRecyclerView() {
        val adapter = HistoryAdapter(orderList) { order -> navigateToOrderDetail(order) }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = false
            stackFromEnd = false
        }
        binding.rvHistory.adapter = adapter
    }

    private fun navigateToOrderDetail(order: OrderModel) {
        val intent = Intent(requireContext(), AdminOrderDetailActivity::class.java).apply {
            putExtra("order", order)
            putExtra("userId", order.userId)
            putExtra("orderId", order.orderId)
        }
        startActivity(intent)
    }

    // Menambahkan fungsi refresh untuk me-refresh data
    fun refreshRecyclerView() {
        observeOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        observeOrders() // Memastikan data terbaru dimuat kembali
    }

    companion object {
        fun newInstance(statusFilter: String) = AdminOrderListFragment().apply {
            arguments = Bundle().apply {
                putString("statusFilter", statusFilter)
            }
        }
    }
}
