package com.example.splitwise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.CreateGroup
import com.example.splitwise.databinding.AddFriendItemBinding
import com.example.splitwise.models.UserModel

class addFriendAdapter(
    private val friendsList: ArrayList<UserModel>,
    private val context: Context,
    private var itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<addFriendAdapter.addFriendViewHolder>() {
    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): addFriendViewHolder {
        val binding =
            AddFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return addFriendViewHolder(binding)
    }


    override fun onBindViewHolder(holder: addFriendViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    inner class addFriendViewHolder(private val binding: AddFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val friend = friendsList[position]
            binding.friendName.text = friend.userName

            binding.friendName.setOnCheckedChangeListener{
                _, isChecked ->
                if(isChecked){
                    itemClickListener.onCheckedCheckbox(position,friend)

                }

            }

        }

    }

    interface ItemClickListener {
        fun onCheckedCheckbox(position: Int,friend:UserModel)
    }


}