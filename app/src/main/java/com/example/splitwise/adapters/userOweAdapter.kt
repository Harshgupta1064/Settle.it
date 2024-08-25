package com.example.splitwise.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.ExpenseDetails
import com.example.splitwise.GroupExpenseFriendDetails
import com.example.splitwise.databinding.UserOweInGroupItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class userOweAdapter(
    private val friendsOwe: ArrayList<MutableMap<String, Int>>,
    friendId: String?,
    private val context: Context
) : RecyclerView.Adapter<userOweAdapter.userOweViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val friendId = friendId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userOweViewHolder {
        val binding = UserOweInGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return userOweViewHolder(binding)
    }


    override fun onBindViewHolder(holder: userOweViewHolder, position: Int) {
        holder.bind(position,friendId)
    }

    override fun getItemCount(): Int {
        return friendsOwe.size
    }
    inner class userOweViewHolder(private val binding: UserOweInGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, friendId: String?) {
            val balance = friendsOwe[position][friendId]
            binding.apply {
                if(balance!!>=0){
                    owe.text = "You Owe"
                    balanceAmount.text = "Rs. ${balance}"
                }
                else{
                    owe.text = "Owes You"
                    balanceAmount.text = "Rs. ${balance*(-1)}"
                }

                rootRef.child("Users").child(friendId!!).child("userName").addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        friendName.text = snapshot.getValue(String::class.java)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                friendCard.setOnClickListener{
                    val intent = Intent(context, GroupExpenseFriendDetails::class.java)
                    intent.putExtra("friendId",friendId)
                    intent.putExtra("friendName",friendName.text)
                    intent.putExtra("balance",balance.toInt())
                    context.startActivity(intent)
                }
            }
        }
    }
}
