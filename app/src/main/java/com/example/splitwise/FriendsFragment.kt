package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.splitwise.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(requireContext(), AddFriend::class.java)
            startActivity(intent)
        }
        return binding.root
    }


}