package com.example.splitwise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.splitwise.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding: ActivityMainBinding
        var viewPager: ViewPager2
        var viewPagerAdapter: ViewPagerAdapter
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPagerAdapter.addFragment(FriendsFragment())
        viewPagerAdapter.addFragment(GroupsFragment())
        viewPager.adapter = viewPagerAdapter


        var tablayout = binding.tabLayout
        TabLayoutMediator(tablayout, viewPager){
            tab, position ->
            if(position==0){
                tab.text = "Friends"
            }
            else if(position==1){
                tab.text = "Groups"
            }
        }.attach()
    }
}