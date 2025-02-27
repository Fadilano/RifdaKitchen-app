package com.submission.rifda_kitchen.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.submission.rifda_kitchen.view.AwaitingPaymentFragment
import com.submission.rifda_kitchen.view.CompletedFragment
import com.submission.rifda_kitchen.view.OnProcessFragment
import com.submission.rifda_kitchen.view.OrderListFragment
import com.submission.rifda_kitchen.view.PendingValidationFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OrderListFragment.newInstance("Menunggu Konfirmasi")
            1 -> OrderListFragment.newInstance("Pesanan Dikonfirmasi, silahkan lakukan pembayaran")
            2 -> OrderListFragment.newInstance("Pesanan Diproses")
            3 -> OrderListFragment.newInstance("Pesanan selesai")
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
