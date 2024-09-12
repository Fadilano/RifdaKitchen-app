package com.submission.rifda_kitchen.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.submission.rifda_kitchen.view.MakananBeratFragment
import com.submission.rifda_kitchen.view.MakananRinganFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MakananBeratFragment()
            1 -> MakananRinganFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
