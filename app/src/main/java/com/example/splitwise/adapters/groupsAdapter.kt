package com.example.splitwise.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.example.splitwise.databinding.GroupsItemBinding
import com.example.splitwise.models.GroupModel

class groupsAdapter(
    private val groupList: ArrayList<GroupModel>,
    private val context: Context
) : RecyclerView.Adapter<groupsAdapter.GroupsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val binding = GroupsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class GroupsViewHolder(private val binding: GroupsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val group = groupList[position]
            binding.apply {
                groupName.text = group.groupName
                noOfMembers.text = "${group.groupMembers?.size} Members"
            }
        }
    }
}
