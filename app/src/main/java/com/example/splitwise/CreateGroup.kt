package com.example.splitwise

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.friendsPreviewAdapter
import com.example.splitwise.adapters.addFriendAdapter
import com.example.splitwise.databinding.ActivityCreateGroupBinding
import com.example.splitwise.databinding.AddFriendInGroupBinding
import com.example.splitwise.models.GroupModel
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class CreateGroup : AppCompatActivity(), addFriendAdapter.ItemClickListener {
    private val binding: ActivityCreateGroupBinding by lazy {
        ActivityCreateGroupBinding.inflate(layoutInflater)
    }
    private val binding2: AddFriendInGroupBinding by lazy {
        AddFriendInGroupBinding.inflate(layoutInflater)
    }
    private lateinit var groupName: String
    private lateinit var auth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private var groupMembersId: ArrayList<String> = arrayListOf()
    private var groupMembersName: ArrayList<String> = arrayListOf()
    private lateinit var addFriendDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference

//      for adding friend in group
        addFriendDialog = Dialog(this)
        addFriendDialog.setContentView(binding2.root)
        addFriendDialog.setCanceledOnTouchOutside(false)
        binding.addFriendButton.setOnClickListener {
            addFriendDialog.show()
        }

        getFriendIds()

    }

    private fun getFriendIds() {
        val userId = auth.currentUser!!.uid
        val friendsList = ArrayList<String>()
        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference.child("Users").child(userId).child("friends")
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (friendId in snapshot.children) {
                    val friend = friendId.getValue(String::class.java)
                    if (friend != null) {
                        friendsList.add(friend)
                    }
                }
                getFriendsData(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateGroup, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getFriendsData(friendsList: ArrayList<String>) {
        val friendsDataList = ArrayList<UserModel>()
        auth = FirebaseAuth.getInstance()
        for (friendId in friendsList) {
            rootRef = Firebase.database.reference.child("Users").child(friendId)
            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friendData = snapshot.getValue(UserModel::class.java)
                    if (friendData != null) {
                        friendsDataList.add(friendData)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CreateGroup, "Data not Retrieved", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }
        setAddFriendAdapter(friendsDataList)
        binding2.createGroupButton.setOnClickListener {
            setAdapterToFriendListPreview()
            addFriendDialog.dismiss()
        }
        binding.createGroupButton.setOnClickListener {
            groupName = binding.groupName.text.toString().trim()
            if (groupMembersName.isNotEmpty() && groupName.isNotEmpty()) {
                setGroupInFirebase()
            } else {
                Toast.makeText(this, "Please Enter Group Name", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun setAddFriendAdapter(friendDataList: ArrayList<UserModel>) {


        val adapter = addFriendAdapter(friendDataList, this, this)
        binding2.addFriendRecyclerView.adapter = adapter
        binding2.addFriendRecyclerView.layoutManager = LinearLayoutManager(this)

    }


    override fun onCheckedCheckbox(position: Int, friend: UserModel) {
        groupMembersId.add(friend.userId!!)
        groupMembersName.add(friend.userName!!)


    }

    private fun setGroupInFirebase() {
        val group: GroupModel = GroupModel()
        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference.child("Groups")
        var groupKey = rootRef.push().key
        group.groupMembers = groupMembersId
        group.groupId = groupKey
        group.groupName = groupName

        if (groupKey != null) {
            rootRef.push().setValue(group).addOnSuccessListener {
                Toast.makeText(this, "group added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "group not added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdapterToFriendListPreview() {
        val adapter = friendsPreviewAdapter(groupMembersName, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
