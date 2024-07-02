package com.example.splitwise.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.splitwise.databinding.FriendsItemBinding
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.splitwise.friendsActivity
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class friendsAdapter(
    private val friendsList: ArrayList<String>, private val context: Context
) : RecyclerView.Adapter<friendsAdapter.friendsViewHolder>() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var rootRef = Firebase.database.reference.child("Users")
    private var selectedFriendName:String = String()
    private var selectedFriendId:String = String()
    private var selectedFriendEmail:String = String()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendsViewHolder {
        val binding = FriendsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return friendsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: friendsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    inner class friendsViewHolder(private val binding: FriendsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val friendId = friendsList[position]
            binding.apply {
                rootRef.child(friendId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val friend = snapshot.getValue(UserModel::class.java)
                        selectedFriendId = friend!!.userId.toString()
                        selectedFriendName = friend.userName.toString()
                        selectedFriendEmail = friend.email.toString()
                        friendName.text = selectedFriendName
                        friendEmail.text = selectedFriendEmail
                        if (friend.userImage != "") {
                            Glide.with(context).load(friend.userImage).into(profileImage)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                })
                friendCardView.setOnClickListener{
                    val intent = Intent(context,friendsActivity::class.java)
                    intent.putExtra("friendName",selectedFriendName)
                    intent.putExtra("friendId",selectedFriendId)
                    context.startActivity(intent)
                }
            }
        }
    }
}
