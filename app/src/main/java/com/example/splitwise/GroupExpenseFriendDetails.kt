package com.example.splitwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splitwise.databinding.ActivityExpenseDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlin.math.abs
import kotlin.properties.Delegates

class GroupExpenseFriendDetails : AppCompatActivity() {
    private val binding: ActivityExpenseDetailsBinding by lazy {
        ActivityExpenseDetailsBinding.inflate(layoutInflater)
    }
    private var members:Int ?=null
    private lateinit var userId: String
    private var friendId: String?=null
    private var friendName:String?=null
    private var balanceAmount:Int?=null
    private var userName: String?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        auth= FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        friendId = intent.getStringExtra("friendId").toString()
        friendName = intent.getStringExtra("friendName").toString()
        balanceAmount = intent.getIntExtra("balance",0)
        binding.dateOfExpense.visibility = View.GONE
        binding.expenseAmount.visibility = View.GONE
        binding.expenseName.visibility = View.GONE
        binding.textView15.visibility = View.GONE
        binding.textView6.visibility = View.GONE
        binding.textView7.visibility = View.GONE
        Firebase.database.reference.child("Users").child(userId).child("userName").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        if(balanceAmount!!>=0){
            binding.owedBy.text = "You"
            binding.owedTo.text = friendName
        }
        else{
            binding.owedBy.text = friendName
            binding.owedTo.text = "You"
        }
        binding.owedAmount.text = abs(balanceAmount!!).toString()

    }
}