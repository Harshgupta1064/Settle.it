package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splitwise.databinding.ActivityAddFriendBinding
import com.example.splitwise.models.FriendModel
import com.example.splitwise.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddFriend : AppCompatActivity() {
    private val binding: ActivityAddFriendBinding by lazy {
        ActivityAddFriendBinding.inflate(layoutInflater)
    }
    private lateinit var friendEmail: String
    private lateinit var friendId: String
    private lateinit var friendName: String
    private var userFriends: ArrayList<FriendModel>? = null
    private var friendPhoneNumber: String? = null
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
            friendName = binding.friendName.text.toString().trim()
            friendEmail = binding.email.text.toString().trim()
            if (!friendEmail.isBlank()) {
                checkFriendEmailUniqueness()
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
                        friendPhoneNumber = user?.phoneNumber
                        userId = auth.currentUser?.uid!!
                        addFriend(userId)
                        addFriend(friendId)
                        startActivity(Intent(this@AddFriend,MainActivity::class.java))
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

    private fun addFriend(userId: String) {
        rootRef.child(this.userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userFriends = snapshot.child("friends")
                        .getValue(object : GenericTypeIndicator<ArrayList<FriendModel>>() {})
                    if (userFriends == null) {
                        userFriends = ArrayList()
                    }
                    userFriends!!.add(
                        FriendModel(
                            friendId = friendId,
                            name = friendName,
                            phoneNumber = friendPhoneNumber,
                            email = friendEmail
                        )
                    )
                    rootRef.child(this@AddFriend.userId).child("friends").setValue(userFriends)
                    Toast.makeText(this@AddFriend, "Friend added successfully", Toast.LENGTH_SHORT)
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

    private fun checkFriendEmailUniqueness() {
        userId = auth.currentUser?.uid!!
        rootRef.child(userId).child("friends")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var emailExists = false
                    for (friendSnapshot in snapshot.children) {
                        val friend = friendSnapshot.getValue(FriendModel::class.java)
                        if (friend?.email == friendEmail) {
                            emailExists = true
                            break
                        }
                    }

                    if (emailExists) {
                        Toast.makeText(this@AddFriend, "Friend with this email already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        fetchFriendData()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddFriend, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
