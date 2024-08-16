package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.friendsAdapter
import com.example.splitwise.adapters.groupsAdapter
import com.example.splitwise.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var adapter: friendsAdapter
    private var friendsList: ArrayList<String> =
        ArrayList() // Change to ArrayList<String> to hold friend IDs
    private lateinit var auth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FriendsFragment", "onCreateView called")
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        rootRef = FirebaseDatabase.getInstance().reference.child("Users")

        adapter = friendsAdapter(friendsList, requireContext())
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerView.adapter = adapter

        retrieveFriendsData()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), AddFriend::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun retrieveFriendsData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        rootRef.child(userId).child("friends")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear() // Clear the list to avoid duplicate entries
                    for (friendSnapshot in snapshot.children) {
                        val friendId: String? = friendSnapshot.getValue(String::class.java)
                        Log.d("Error", "${friendSnapshot.value}")
                        if (friendId != null) {
                            friendsList.add(friendId)
                        } else {
                            Log.e(
                                "FriendsFragment",
                                "Failed to convert friend to String: ${friendSnapshot.value}"
                            )
                        }
                    }
                    // Notify adapter on the main thread
                    Handler(Looper.getMainLooper()).post {
                        adapter.notifyDataSetChanged()
                    }
                    friendsList.reverse()
                    setAdapter(friendsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Currently not able to fetch Friends: ${error.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setAdapter(friendsList: java.util.ArrayList<String>) {
        if (friendsList != null) {
            adapter = friendsAdapter(friendsList,requireContext())
            binding.friendRecyclerView.adapter = adapter
            binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
