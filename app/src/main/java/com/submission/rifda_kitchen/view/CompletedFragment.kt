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
import com.submission.rifda_kitchen.databinding.FragmentCompletedBinding
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.HistoryViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory


class CompletedFragment : Fragment() {
    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!
    private val repository = Repository()
    private val historyViewModel: HistoryViewmodel by viewModels { ViewmodelFactory(repository) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
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
            reverseLayout = true
        }
    }



    private fun observeViewModel() {
        historyViewModel.orderList.observe(viewLifecycleOwner) { orders ->
            binding.progressBar.visibility = View.GONE  // Sembunyikan progress bar saat data sudah dimuat
            val filteredOrders = orders?.filter { it.orderStatus == "Pesanan selesai" }

            if (filteredOrders.isNullOrEmpty()) {
                binding.tvNoItem.visibility = View.VISIBLE  // Tampilkan pesan "No Item"
                binding.rvHistory.visibility = View.GONE    // Sembunyikan RecyclerView
            } else {
                binding.tvNoItem.visibility = View.GONE     // Sembunyikan pesan "No Item"
                binding.rvHistory.visibility = View.VISIBLE // Tampilkan RecyclerView
                (binding.rvHistory.adapter as HistoryAdapter).updateList(filteredOrders)
            }
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
