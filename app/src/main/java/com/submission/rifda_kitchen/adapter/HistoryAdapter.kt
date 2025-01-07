package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.submission.rifda_kitchen.databinding.HistoryItemListBinding
import com.submission.rifda_kitchen.model.OrderModel

class HistoryAdapter(
    private var orders: List<OrderModel>,
    private val onItemClick: (OrderModel) -> Unit
) :
    RecyclerView.Adapter<HistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: HistoryItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderModel) {
            binding.tvCustName.text = order.name
            binding.tvOrderDate.text = order.date
            binding.tvconfirmationStatus.text = order.orderStatus
            binding.btnToDetail.setOnClickListener {
                onItemClick(order)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            HistoryItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateList(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
