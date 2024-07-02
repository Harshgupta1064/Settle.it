package com.example.splitwise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.databinding.FriendListItemBinding
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class friendListAdapter(private val groupMembers: ArrayList<String>, private val context: Context) :
    RecyclerView.Adapter<friendListAdapter.friendListViewHolder>() {
    private var friendRef = Firebase.database.reference.child("Users")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendListViewHolder {
        val binding =
            FriendListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return friendListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: friendListViewHolder, position: Int) {
        val friendId = groupMembers[position]
        friendRef.child(friendId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(UserModel::class.java)
                if (friend != null) {
                    holder.friendName.text = friend.userName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return groupMembers.size
    }

    inner class friendListViewHolder(binding: FriendListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val friendName = binding.friendName
    }


}