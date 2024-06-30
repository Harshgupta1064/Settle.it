package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.groupsAdapter
import com.example.splitwise.databinding.FragmentGroupsBinding
import com.example.splitwise.models.GroupModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class GroupsFragment : Fragment() {
    private lateinit var binding : FragmentGroupsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private var groupIdList:ArrayList<String>?=null
    private var adapter:groupsAdapter?=null

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("GroupsFragment", "onCreateView called")
        binding = FragmentGroupsBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference.child("Users")
        retrieveGroupsData()
        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(requireContext(),CreateGroup::class.java))
        }
        return binding.root
    }

    private fun retrieveGroupsData() {
        val userId = auth.currentUser!!.uid
        rootRef.child(userId).child("groups").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                groupIdList?.clear()
                for(group in snapshot.children){
                    val groupId: String? = group.getValue(String::class.java)
                    if (groupId != null) {
                        groupIdList!!.add(groupId)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Groups Not loading Currently", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter() {
        if(groupIdList!=null){
            adapter = groupsAdapter(groupIdList!!, requireContext())
            binding.groupRecyclerView.adapter = adapter
            binding.groupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }


}