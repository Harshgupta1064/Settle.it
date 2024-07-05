package com.example.splitwise

import android.app.Dialog
import android.content.Intent
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
    private var groupKey: String = String()
    private var groupMembersId: ArrayList<String> = arrayListOf()
    private var groupMembersName: ArrayList<String> = arrayListOf()
    private lateinit var addFriendDialog: Dialog
    private lateinit var uniqueGroupId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        uniqueGroupId = String()
//      for adding friend in group
        addFriendDialog = Dialog(this)
        addFriendDialog.setContentView(binding2.root)
        addFriendDialog.setCanceledOnTouchOutside(false)
        binding.addFriendButton.setOnClickListener {
            addFriendDialog.show()
        }

//      adding user to the group
        addUserToGroup()


//      setting all the data
        getFriendIds()

    }

    private fun addUserToGroup() {
        val userAuth = FirebaseAuth.getInstance()
        val userId = userAuth.currentUser!!.uid
        groupMembersName.add("You")
        groupMembersId.add(userId)

    }

    private fun getFriendIds() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val friendsList = ArrayList<String>()
        val rootRef = Firebase.database.reference.child("Users").child(userId).child("friends")
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
        val auth = FirebaseAuth.getInstance()
        for (friendId in friendsList) {
            val rootRef = Firebase.database.reference.child("Users").child(friendId)
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
        binding2.AddFriendButton.setOnClickListener {
            setAdapterToFriendListPreview()
            addFriendDialog.dismiss()
        }
        binding.createGroupButton.setOnClickListener {
            groupName = binding.groupName.text.toString().trim()
            if (groupMembersName.isNotEmpty() && groupName.isNotEmpty()) {
                setGroupInFirebase()
                setGroupIdToUsers()
            } else {
                Toast.makeText(this, "Please Enter Group Name", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this,MainActivity::class.java))
        }


    }


    private fun setAddFriendAdapter(friendDataList: ArrayList<UserModel>) {


        val adapter = addFriendAdapter(friendDataList, this, this)
        binding2.addFriendRecyclerView.adapter = adapter
        binding2.addFriendRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    override fun onCheckedCheckbox( friend: UserModel) {
        if (!groupMembersId.contains(friend.userId)) {
            groupMembersId.add(friend.userId!!)
            groupMembersName.add(friend.userName!!)

        }
    }
    override fun onUncheckedCheckbox(member: UserModel) {
        if (groupMembersId.contains(member.userId!!)) {
            groupMembersId.remove(member.userId!!)
            groupMembersId.remove(member.userName!!)
        }
    }

    private fun setGroupInFirebase() {
        val group: GroupModel=GroupModel()
        val auth = FirebaseAuth.getInstance()
        val rootRef = Firebase.database.reference.child("Groups")
        groupKey = rootRef.push().key.toString()
        uniqueGroupId = groupKey
        group.groupMembers = groupMembersId
        group.groupId = groupKey
        group.groupName = groupName

        if (groupKey != null) {
            rootRef.child(groupKey).setValue(group).addOnSuccessListener {
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

    private fun setGroupIdToUsers() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        val rootRef = Firebase.database.reference.child("Users")
        for (memberId in groupMembersId) {
            val memberGroupList: ArrayList<String> = arrayListOf()
            rootRef.child(memberId).child("groups")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (group in snapshot.children) {
                            val memberGroupId = group.getValue(String::class.java)
                            if (memberGroupId != null) {
                                memberGroupList.add(memberGroupId)
                            }
                        }

                        memberGroupList.add(uniqueGroupId)
                        rootRef.child(memberId).child("groups").setValue(memberGroupList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@CreateGroup, "Something went Wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }
}