package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splitwise.databinding.ActivityAddFriendBinding
import com.example.splitwise.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddFriend : AppCompatActivity() {
    private val binding: ActivityAddFriendBinding by lazy {
        ActivityAddFriendBinding.inflate(layoutInflater)
    }
    private lateinit var friendEmail: String
    private lateinit var friendId: String
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        rootRef = database.reference.child("Users")

        binding.addFriend.setOnClickListener {
            friendEmail = binding.email.text.toString().trim()
            if (friendEmail.isNotBlank()) {
                fetchFriendData()
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchFriendData() {
        val query = rootRef.orderByChild("email").equalTo(friendEmail)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        friendId = childSnapshot.key ?: "No ID found"
                        val user = childSnapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            checkFriendEmailUniqueness()
                        }
                    }
                } else {
                    Toast.makeText(this@AddFriend, "Friend not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AddFriend,
                    "Database error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addFriend(userId: String, friendId: String) {
        val userFriendsRef = rootRef.child(userId).child("friends")

        userFriendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Retrieve the current list of friends
                val friendsList = snapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()

                if (!friendsList.contains(friendId)) {
                    friendsList.add(friendId)

                    // Update the friends list in the database
                    userFriendsRef.setValue(friendsList).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@AddFriend, "Friend added successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@AddFriend, "Failed to add friend: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AddFriend, "Friend already exists.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddFriend, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Also add friendId to the friend's friends list
        val friendFriendsRef = rootRef.child(friendId).child("friends")
        friendFriendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList = snapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()

                if (!friendList.contains(userId)) {
                    friendList.add(userId)
                    friendFriendsRef.setValue(friendList).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@AddFriend, "Friend added successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@AddFriend, "Failed to add friend: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddFriend, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkFriendEmailUniqueness() {
        userId = auth.currentUser?.uid!!
        rootRef.child(userId).child("friends")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var emailExists = false
                    for (friendSnapshot in snapshot.children) {
                        if (friendSnapshot.getValue(String::class.java) == friendId) {
                            emailExists = true
                            break
                        }
                    }

                    if (emailExists) {
                        Toast.makeText(this@AddFriend, "Friend with this email already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // Save friend to user data
                        addFriend(userId, friendId)
                        // Save user to friend data
                        addFriend(friendId, userId)
                        startActivity(Intent(this@AddFriend, MainActivity::class.java))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddFriend, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
