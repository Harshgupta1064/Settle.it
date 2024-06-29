package com.example.splitwise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.databinding.AddFriendItemBinding

class createGroupAdapter(private val friends: ArrayList<String>, private val context: Context) :
    RecyclerView.Adapter<createGroupAdapter.createGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): createGroupViewHolder {
        val binding =
            AddFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return createGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: createGroupViewHolder, position: Int) {
        holder.friend.text = friends.get(position)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    class createGroupViewHolder(binding: AddFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var friend = binding.friendName
    }
}