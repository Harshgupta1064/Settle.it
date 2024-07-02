package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splitwise.databinding.ActivityGroupsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class GroupsActivity : AppCompatActivity() {
    private val binding: ActivityGroupsBinding by lazy {
        ActivityGroupsBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var expenseRef:DatabaseReference
    private var selectedGroupName:String = String()
    private var selectedGroupId :String = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        selectedGroupName = intent.getStringExtra("groupName").toString()
        selectedGroupId = intent.getStringExtra("groupId").toString()
        binding.groupName.text = selectedGroupName
        binding.expenseFAB.setOnClickListener{
            val intentExpense = Intent(this,ExpenseActivity::class.java)
            intentExpense.putExtra("groupName",selectedGroupName)
            intentExpense.putExtra("groupId",selectedGroupId)
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

    }
}