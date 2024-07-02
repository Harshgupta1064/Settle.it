package com.example.splitwise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivityFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class friendsActivity : AppCompatActivity() {
    private val binding: ActivityFriendsBinding by lazy {
        ActivityFriendsBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var expenseRef: DatabaseReference
    private var selectedFriendName:String = String()
    private var selectedFriendId :String = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        selectedFriendName = intent.getStringExtra("friendName").toString()
        selectedFriendId = intent.getStringExtra("friendId").toString()
        binding.friendNameHeading.text = selectedFriendName

    }
}