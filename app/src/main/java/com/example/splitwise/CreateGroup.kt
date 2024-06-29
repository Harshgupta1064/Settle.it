package com.example.splitwise

import android.app.Dialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivityCreateGroupBinding
import com.example.splitwise.models.GroupModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class CreateGroup : AppCompatActivity() {
    private val binding: ActivityCreateGroupBinding by lazy {
        ActivityCreateGroupBinding.inflate(layoutInflater)
    }
    private lateinit var groupName:String
    private lateinit var auth:FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private var groupMembersName:ArrayList<String>?=null
    private var groupMembersId:ArrayList<String>?=null
    private lateinit var addFriendDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        groupName = binding.groupName.text.toString()
        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference

//      for adding friend in group
        addFriendDialog = Dialog(this)
        addFriendDialog.setContentView(R.layout.add_friend_in_group)
        binding.addFriendButton.setOnClickListener{
            addFriendDialog.show()
        }
        createGroup()

    }

    private fun createGroup() {

    }
}