package com.example.splitwise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.userOweAdapter
import com.example.splitwise.databinding.ActivityUserOweInGroupBinding
import com.example.splitwise.models.BalanceAmountDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class UserOweInGroupActivity : AppCompatActivity() {
    val binding : ActivityUserOweInGroupBinding by lazy {
        ActivityUserOweInGroupBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var groupRef: DatabaseReference
    private lateinit var groupId:String
    private lateinit var groupName:String
    private lateinit var adapter:userOweAdapter
    private var friendOwe : MutableMap<String,Int> = mutableMapOf()
    private var friendsOwe : ArrayList<MutableMap<String,Int>> = arrayListOf()
    private var balances:ArrayList<BalanceAmountDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        auth = FirebaseAuth.getInstance()
        groupRef = Firebase.database.reference
        groupId = intent.getStringExtra("groupId").toString()
        groupName = intent.getStringExtra("groupName").toString()

        groupRef.child("Balances").addListenerForSingleValueEvent(object: ValueEventListener {
            var friendId:String?=null
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(balance in snapshot.children){
                        val oneBalance = balance.getValue(BalanceAmountDetails::class.java)!!
                        if(oneBalance.groupId==groupId && (oneBalance.owedBy==auth.currentUser!!.uid || oneBalance.owedTo==auth.currentUser!!.uid)){
                            friendId = if (oneBalance.owedBy == auth.currentUser!!.uid) {
                                oneBalance.owedTo // Friend's ID
                            } else {
                                oneBalance.owedBy // Friend's ID
                            }
                            friendOwe.putIfAbsent(friendId!!, 0)
                            if (oneBalance.owedBy == auth.currentUser!!.uid) {
                                friendOwe[friendId!!] = friendOwe[friendId]!! + oneBalance.amount!!.toInt()
                            }
                            // If friend owes, subtract from friend's balance
                            else if (oneBalance.owedTo == auth.currentUser!!.uid) {
                                friendOwe[friendId!!] = friendOwe[friendId]!! - oneBalance.amount!!.toInt()
                            }
                        }
                    }
                    friendsOwe.add(friendOwe)
                    setAdapter(friendsOwe,friendId)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        




    }

    private fun setAdapter(friendsOwe: ArrayList<MutableMap<String, Int>>, friendId: String?) {
        adapter = userOweAdapter(friendsOwe,friendId,this)
        binding.userOweRecyclerView.adapter = adapter
        binding.userOweRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}