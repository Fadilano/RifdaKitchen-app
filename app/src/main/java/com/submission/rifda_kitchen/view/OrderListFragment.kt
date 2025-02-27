package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.databinding.FragmentOrderListBinding
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.HistoryViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class OrderListFragment : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private val repository = Repository()
    private val historyViewModel: HistoryViewmodel by viewModels { ViewmodelFactory(repository) }

    private var statusFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusFilter = it.getString("statusFilter")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userUID = repository.getCurrentUser()?.uid

        historyViewModel.fetchOrdersForUser(userUID.toString())
        setupRecyclerView()


        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = HistoryAdapter(emptyList()) { order -> navigateToOrderDetail(order) }
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = false
            stackFromEnd = false
        }
    }

    private fun observeViewModel() {
        historyViewModel.orderList.observe(viewLifecycleOwner) { orders ->
            val filteredOrders = orders?.filter { it.orderStatus == statusFilter }

            if (filteredOrders.isNullOrEmpty()) {
                binding.tvNoItem.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvNoItem.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                (binding.rvHistory.adapter as HistoryAdapter).updateList(filteredOrders)
            }

            binding.progressBar.visibility = View.GONE
        }
    }


    private fun navigateToOrderDetail(order: OrderModel) {
        val intent = Intent(requireContext(), OrderDetailActivity::class.java).apply {
            putExtra("order", order)
            putExtra("userId", order.userId)
            putExtra("orderId", order.orderId)        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(statusFilter: String) = OrderListFragment().apply {
            arguments = Bundle().apply {
                putString("statusFilter", statusFilter)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        historyViewModel.fetchOrdersForUser(repository.getCurrentUser()?.uid ?: "")
    }

}
