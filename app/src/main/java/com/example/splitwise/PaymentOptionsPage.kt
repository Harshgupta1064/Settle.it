package com.example.splitwise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivityPaymentOptionsPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class PaymentOptionsPage : AppCompatActivity() {
    val binding : ActivityPaymentOptionsPageBinding by lazy {
        ActivityPaymentOptionsPageBinding.inflate(layoutInflater)
    }
    private lateinit var expenseId: String
    private lateinit var expenseName: String
    private lateinit var dateOfExpense: String
    private lateinit var expensePaidBy: String
    private lateinit var expenseAmount: String
    private var members by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}