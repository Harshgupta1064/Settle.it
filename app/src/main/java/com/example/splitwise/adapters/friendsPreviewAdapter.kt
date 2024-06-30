package com.example.splitwise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.databinding.FriendPreviewItemBinding

class friendsPreviewAdapter(private val friendsNameList: ArrayList<String>, context: Context) :
    RecyclerView.Adapter<friendsPreviewAdapter.friendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendsViewHolder {
        val binding = FriendPreviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return friendsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: friendsViewHolder, position: Int) {
        holder.friendName.text = friendsNameList[position]
    }
    override fun getItemCount(): Int {
        return friendsNameList.size
    }

    inner class friendsViewHolder(private val binding: FriendPreviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val friendName  = binding.friendName
    }
}