package com.example.splitwise

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivitySettleUpPageBinding

class SettleUpPage : AppCompatActivity() {
    val binding : ActivitySettleUpPageBinding by lazy {
        ActivitySettleUpPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


    }
}