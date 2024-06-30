package com.example.splitwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.splitwise.databinding.ActivityDrawerBinding
import com.example.splitwise.databinding.ActivityMainBinding

class DrawerActivity : AppCompatActivity() {
    private lateinit var menuButton: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_drawer)
        drawerLayout = findViewById(R.id.drawerLayout)
        menuButton = findViewById(R.id.sideDrawerButton)

    }
}