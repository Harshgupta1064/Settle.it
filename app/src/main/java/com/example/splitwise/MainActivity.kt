package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.splitwise.databinding.ActivityMainBinding
import com.example.splitwise.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPagerAdapter.addFragment(FriendsFragment())
        viewPagerAdapter.addFragment(GroupsFragment())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Friends"
                1 -> tab.text = "Groups"
            }
        }.attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("TabLayout", "Tab selected: ${tab.text}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.d("TabLayout", "Tab unselected: ${tab.text}")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.d("TabLayout", "Tab reselected: ${tab.text}")
            }
        })
    }
}
