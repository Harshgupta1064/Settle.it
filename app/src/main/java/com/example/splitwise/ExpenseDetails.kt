package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivityExpenseDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlin.properties.Delegates

class ExpenseDetails : AppCompatActivity() {
    private val binding: ActivityExpenseDetailsBinding by lazy {
        ActivityExpenseDetailsBinding.inflate(layoutInflater)
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
        binding.backButton.setOnClickListener {
            finish()
        }
        userId = auth.currentUser!!.uid
        expenseId = intent.getStringExtra("expenseId").toString()
        expenseName = intent.getStringExtra("expenseName").toString()
        dateOfExpense = intent.getStringExtra("expenseDate").toString()
        expensePaidBy = intent.getStringExtra("expensePaidBy").toString()
        expenseAmount = intent.getStringExtra("expenseAmount").toString()
        members = intent.getIntExtra("owedAmount", 0)

        binding.expenseName.text = expenseName
        binding.dateOfExpense.text = dateOfExpense
        binding.owedTo.text = expensePaidBy
        binding.owedBy.text = userId
        binding.expenseAmount.text = expenseAmount
        binding.owedAmount.text = expenseAmount
        binding.settleItButton.setOnClickListener {
            val intent = Intent(this, PaymentOptionsPage::class.java)
            intent.putExtra("expenseId", expenseId)
            intent.putExtra("expenseName", expenseName)
            intent.putExtra("expenseDate", dateOfExpense)
            intent.putExtra("expensePaidBy", expensePaidBy)
            intent.putExtra("expenseAmount", expenseAmount)
            intent.putExtra("owedAmount", members)
            startActivity(intent)
        }



    }
}