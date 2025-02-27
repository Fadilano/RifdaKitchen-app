package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.submission.rifda_kitchen.databinding.LatestOrderListBinding
import com.submission.rifda_kitchen.model.OrderModel

class LatestOrderAdapter(
    private var orders: List<OrderModel>,
    private val onClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<LatestOrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: LatestOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderModel) {
            binding.tvOrderName.text = order.name
            binding.tvOrderDate.text = order.date
            binding.tvOrderStatus.text = order.orderStatus

            binding.root.setOnClickListener {
                onClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = LatestOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
