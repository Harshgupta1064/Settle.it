package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.expenseAdapter
import com.example.splitwise.databinding.ActivityGroupsBinding
import com.example.splitwise.models.ExpenseModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class GroupsActivity : AppCompatActivity() {
    private val binding: ActivityGroupsBinding by lazy {
        ActivityGroupsBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var expenseRef:DatabaseReference
    private lateinit var adapter: expenseAdapter
    private var selectedGroupName:String = String()
    private var selectedGroupId :String = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        expenseRef = Firebase.database.reference
        selectedGroupName = intent.getStringExtra("groupName").toString()
        selectedGroupId = intent.getStringExtra("groupId").toString()
        binding.groupName.text = selectedGroupName
        binding.expenseFAB.setOnClickListener{
            val intentExpense = Intent(this,ExpenseActivity::class.java)
            intentExpense.putExtra("Name",selectedGroupName)
            intentExpense.putExtra("Id",selectedGroupId)
            startActivity(intentExpense)
        }
        binding.friendsListButton.setOnClickListener{
            val intentFriendList = Intent(this,FriendsListActivity::class.java)
            intentFriendList.putExtra("groupName",selectedGroupName)
            intentFriendList.putExtra("groupId",selectedGroupId)
            startActivity(intentFriendList)
        }
        binding.howMuchYouOweButton.setOnClickListener{
            val intentOweMoney = Intent(this,UserOweInGroupActivity::class.java)
            intentOweMoney.putExtra("groupName",selectedGroupName)
            intentOweMoney.putExtra("groupId",selectedGroupId)
            startActivity(intentOweMoney)
        }
        fetchExpenseData()


    }


    override fun onResume() {
        super.onResume()
        fetchExpenseData()
    }


    private fun fetchExpenseData() {
        val expenseList : ArrayList<ExpenseModel> = arrayListOf()
        expenseRef.child("Expenses").orderByChild("dateOfExpense").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(oneExpense in snapshot.children) {
                    val expense = oneExpense.getValue(ExpenseModel::class.java)
                    if (expense != null && expense.groupId == selectedGroupId) {
                        expenseList.add(expense)
                    }
                }
                expenseList.reverse()
                setAdapter(expenseList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GroupsActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter(expenseList: ArrayList<ExpenseModel>) {
        adapter = expenseAdapter(expenseList=expenseList, context = this)
        binding.expenseRecyclerView.adapter = adapter
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}