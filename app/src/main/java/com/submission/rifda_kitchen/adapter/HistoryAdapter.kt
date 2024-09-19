package com.submission.rifda_kitchen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.submission.rifda_kitchen.databinding.HistoryItemListBinding
import com.submission.rifda_kitchen.model.OrderModel

class HistoryAdapter(private var orders: List<OrderModel>) : RecyclerView.Adapter<HistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: HistoryItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = HistoryItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.tvOrderDate.text = order.date

        val confirmationStatus = if (order.confirmationStatus) {
            if (order.paymentStatus) "Pesanan di proses" else "Pesanan di konfirmasi, silahkan lakukan pembayaran"
        } else {
            "Menunggu konfirmasi"
        }

        holder.binding.tvconfirmationStatus.text = confirmationStatus
    }

    override fun getItemCount(): Int = orders.size

    fun updateList(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
