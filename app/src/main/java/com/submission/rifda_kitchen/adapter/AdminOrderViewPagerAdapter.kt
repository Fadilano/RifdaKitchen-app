package com.submission.rifda_kitchen.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.submission.rifda_kitchen.admin.view.AdminOrderListActivity
import com.submission.rifda_kitchen.admin.view.AdminOrderListFragment
import com.submission.rifda_kitchen.view.OrderListFragment

class AdminOrderViewPagerAdapter(activity: AdminOrderListActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminOrderListFragment.newInstance("Menunggu Konfirmasi")
            1 -> AdminOrderListFragment.newInstance("Pesanan Dikonfirmasi, silahkan lakukan pembayaran")
            2 -> AdminOrderListFragment.newInstance("Pesanan Diproses")
            3 -> AdminOrderListFragment.newInstance("Pesanan selesai")
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}