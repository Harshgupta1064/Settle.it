package com.example.splitwise

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.friendListAdapter
import com.example.splitwise.databinding.ActivityFriendsListBinding
import com.example.splitwise.databinding.ActivityGroupsBinding
import com.example.splitwise.models.GroupModel
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.ArrayList

class FriendsListActivity : AppCompatActivity() {
    private val binding: ActivityFriendsListBinding by lazy {
        ActivityFriendsListBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var friendRef: DatabaseReference
    private var selectedGroupName:String = ""
    private var selectedGroupId : String = ""
    private var groupMemberList :ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        friendRef = Firebase.database.reference.child("Groups")
        selectedGroupName = intent.getStringExtra("groupName").toString()
        selectedGroupId = intent.getStringExtra("groupId").toString()
        binding.groupButtonHeading.text = "${selectedGroupName}/Friend List"
        retrieveFriendsData()

    }

    private fun retrieveFriendsData() {
        friendRef.child(selectedGroupId).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val group:GroupModel? = snapshot.getValue(GroupModel::class.java)
                if (group != null) {
                    groupMemberList = group.groupMembers ?: arrayListOf()
                    setAdapter()
                } else {
                    // Handle the case where group is null
                    Toast.makeText(this@FriendsListActivity, "Failed to retrieve group data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {
        val adapter = friendListAdapter(groupMemberList,this)
        binding.friendRecyclerView.adapter = adapter
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}