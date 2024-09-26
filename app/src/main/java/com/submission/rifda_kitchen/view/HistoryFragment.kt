package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.databinding.FragmentHistoryBinding
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.HistoryViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val repository = Repository()
    private val historyViewModel: HistoryViewmodel by viewModels { ViewmodelFactory(repository) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
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
        val adapter = HistoryAdapter(emptyList()) { order ->
            navigateToOrderDetail(order)  // Navigate to OrderDetailActivity when an item is clicked
        }
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        historyViewModel.orderList.observe(viewLifecycleOwner) { orders ->
            if (orders != null) {
                (binding.rvHistory.adapter as HistoryAdapter).updateList(orders)
            }
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    private fun navigateToOrderDetail(order: OrderModel) {
        val intent = Intent(requireContext(), OrderDetailActivity::class.java).apply {
            putExtra("order", order)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
