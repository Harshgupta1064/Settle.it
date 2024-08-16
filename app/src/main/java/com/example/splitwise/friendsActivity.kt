package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.FriendsExpenseAdapter
import com.example.splitwise.databinding.ActivityFriendsBinding
import com.example.splitwise.models.BalanceAmountDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class friendsActivity : AppCompatActivity() {
    private val binding: ActivityFriendsBinding by lazy {
        ActivityFriendsBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var expenseRef: DatabaseReference
    private lateinit var adapter: FriendsExpenseAdapter
    private var balanceAmount: BalanceAmountDetails?=null
    private var selectedFriendName: String = String()
    private var selectedFriendId: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        expenseRef = Firebase.database.reference
        selectedFriendName = intent.getStringExtra("friendName").toString()
        selectedFriendId = intent.getStringExtra("friendId").toString()
        binding.addExpenseFAB.setOnClickListener {
            val intentExpense = Intent(this, FriendsExpenseActivity::class.java)
            intentExpense.putExtra("friendName", selectedFriendName)
            intentExpense.putExtra("friendId", selectedFriendId)
            startActivity(intentExpense)
        }
        binding.friendNameHeading.text = selectedFriendName
        fetchExpenseData()

    }

    override fun onResume() {
        super.onResume()
        fetchExpenseData()
    }
    private fun fetchExpenseData() {
        val balancesList: ArrayList<BalanceAmountDetails> = arrayListOf()
//      Here in the saved expense and balance database friendId will be saved as groupId ---> for adding Friend's Expense
        balancesList.clear()
        expenseRef.child("Balances").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (balance in snapshot.children) {
                    balanceAmount = balance.getValue(BalanceAmountDetails::class.java)
                    if (balanceAmount != null && ((balanceAmount!!.owedTo == auth.currentUser!!.uid) && (balanceAmount!!.owedBy == selectedFriendId) )|| ((balanceAmount!!.owedBy == auth.currentUser!!.uid) && (balanceAmount!!.owedTo == selectedFriendId))) {
                        balancesList.add(balanceAmount!!)
                    }
                }
                setAdapter(balancesList)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@friendsActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun setAdapter(balancesList: ArrayList<BalanceAmountDetails>) {
        adapter = FriendsExpenseAdapter(balancesList, this)
        binding.expenseRecyclerView.adapter = adapter
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}