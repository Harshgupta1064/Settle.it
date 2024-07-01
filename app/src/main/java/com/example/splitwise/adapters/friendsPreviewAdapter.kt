package com.example.splitwise.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.databinding.FriendPreviewItemBinding

class friendsPreviewAdapter(private val friendsList : ArrayList<String>,private val context:Context):RecyclerView.Adapter<friendsPreviewAdapter.friendsPreviewViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendsPreviewViewHolder {
        val binding = FriendPreviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return friendsPreviewViewHolder(binding)
    }


    override fun onBindViewHolder(holder: friendsPreviewViewHolder, position: Int) {
        val friend = friendsList.get(position)
        holder.friendName.text = friend

    }
    override fun getItemCount(): Int {
        return friendsList.size
    }

    inner class friendsPreviewViewHolder(binding:FriendPreviewItemBinding):RecyclerView.ViewHolder(binding.root) {
        val friendName = binding.friendName
    }
}
