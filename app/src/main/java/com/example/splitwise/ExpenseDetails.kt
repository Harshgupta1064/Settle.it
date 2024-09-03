package com.example.splitwise

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import kotlin.math.abs
import kotlin.properties.Delegates

class ExpenseDetails : AppCompatActivity(), PaymentStatusListener {
    private val binding: ActivityExpenseDetailsBinding by lazy {
        ActivityExpenseDetailsBinding.inflate(layoutInflater)
    }
    private lateinit var expenseId: String
    private lateinit var expenseName: String
    private lateinit var dateOfExpense: String
    private lateinit var expensePaidBy: String
    private lateinit var expenseAmount: String
    private lateinit var owedAmount:String
    private var members:Int ?=null
    private lateinit var userId: String
    private var friendId: String?=null
    private var friendName: String?=null
    private var userName: String?=null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        auth= FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        expenseId = intent.getStringExtra("expenseId").toString()
        expenseName = intent.getStringExtra("expenseName").toString()
        dateOfExpense = intent.getStringExtra("expenseDate").toString()
        expensePaidBy = intent.getStringExtra("expensePaidBy").toString()
        expenseAmount = intent.getStringExtra("expenseAmount").toString()
        members = intent.getIntExtra("members",0)
        owedAmount = intent.getStringExtra("owedAmount").toString()
        friendId = intent.getStringExtra("expenseOwedBy")


        if(friendId==null){
            binding.owedBy.text = "You"
            binding.expenseName.text = expenseName
            binding.dateOfExpense.text = dateOfExpense
            binding.owedTo.text = expensePaidBy
            binding.expenseAmount.text = expenseAmount
            if(members!=0) {
                owedAmount = (expenseAmount.toInt() / members!!.toInt()).toString()
            }
            binding.owedAmount.text = owedAmount
        }
        else {
            Firebase.database.reference.child("Users").child(friendId!!).child("userName").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        friendName = snapshot.getValue(String::class.java)!!
                        binding.owedBy.text = friendName
                        binding.owedTo.text = "You"
                        binding.expenseName.text = expenseName
                        binding.dateOfExpense.text = dateOfExpense
                        binding.expenseAmount.text = expenseAmount
                        if(members!=0) {
                            owedAmount = (expenseAmount.toInt() / members!!.toInt()).toString()
                        }
                        binding.owedAmount.text = owedAmount
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

//        binding.owedAmount.text = (expenseAmount.toInt()/ members!!).toString()


        binding.settleItButton.setOnClickListener {
            initiateUpiPayment()


        }






    }
    private fun initiateUpiPayment() {




            val easyUpiPayment = EasyUpiPayment(this) {
                this.payeeVpa = "hg19102004@okaxis"
                this.payeeName = "Harsh Gupta"
                this.payeeMerchantCode = "12345"
                this.transactionId = "T2020090212345"
                this.transactionRefId = "T2020090212345"
                this.description = "Description"
                this.amount = "101.00"
            }

            easyUpiPayment.setPaymentStatusListener(this)
            easyUpiPayment.startPayment()

    }



    override fun onTransactionCancelled() {
        Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        Toast.makeText(this, "Transaction Successful  ${transactionDetails}", Toast.LENGTH_SHORT).show()
    }

}