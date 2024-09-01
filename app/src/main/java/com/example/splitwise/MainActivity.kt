package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.splitwise.databinding.ActivityMainBinding
import com.example.splitwise.adapters.ViewPagerAdapter
import com.example.splitwise.models.BalanceAmountDetails
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var rootRef: DatabaseReference


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
        setOwedAmountData()

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

    private fun setOwedAmountData() {
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        rootRef = FirebaseDatabase.getInstance().reference
        var owedToMeAmount : Int = 0
        var owedByMeAmount : Int = 0
        var netBalanceAmount : Int = 0
        var userName : String = "User"
        rootRef.child("Balances").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(balance in snapshot.children){
                    val balanceData = balance.getValue(BalanceAmountDetails::class.java)
                    if(balanceData!!.owedBy == userId || balanceData.owedTo == userId){
                        if(balanceData.owedBy == userId){
                            owedByMeAmount += balanceData.amount!!.toInt()
                        }else{
                            owedToMeAmount += balanceData.amount!!.toInt()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        netBalanceAmount = owedToMeAmount - owedByMeAmount
        binding.owedToMeAmount.text = owedToMeAmount.toString()
        binding.owedByMeAmount.text = owedByMeAmount.toString()
        binding.netBalanceAmount.text = netBalanceAmount.toString()
        rootRef.child("Users").child(userId).child("userName").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.getValue(String::class.java).toString()
                Toast.makeText(this@MainActivity, "$userName", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        binding.userName.text = "Hey $userName"

    }

}
