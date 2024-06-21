package com.example.splitwise

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fm:FragmentManager,lc:Lifecycle) : FragmentStateAdapter(fm,lc) {

    var fragmentList:ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList.get(position)
    }
    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

}