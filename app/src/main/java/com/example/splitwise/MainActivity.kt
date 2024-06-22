package com.example.splitwise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.splitwise.databinding.ActivityMainBinding
import com.example.splitwise.models.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPagerAdapter.addFragment(FriendsFragment())
        viewPagerAdapter.addFragment(GroupsFragment())
        viewPager.adapter = viewPagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Friends"
                1 -> tab.text = "Groups"
            }
        }.attach()
    }
}
