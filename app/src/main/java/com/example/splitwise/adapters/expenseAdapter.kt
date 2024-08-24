package com.example.splitwise.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.ExpenseDetails
import com.example.splitwise.databinding.ExpenseItemBinding
import com.example.splitwise.models.BalanceAmountDetails
import com.example.splitwise.models.ExpenseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class expenseAdapter(
    private val expenseList: ArrayList<ExpenseModel>,
    private val context: Context
) : RecyclerView.Adapter<expenseAdapter.ExpenseViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference
    private var paidByName:String?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {

        return expenseList.size

    }

    inner class ExpenseViewHolder(private val binding: ExpenseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            val expense = expenseList[position]
            binding.apply {
                expenseName.text = expense.expenseName
                expenseAmount.text = expense.amount.toString()
                dateOfExpense.text = expense.dateOfExpense
                rootRef.child("Users").child(expense.paidBy!!).child("userName")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            paidByName = snapshot.getValue(String::class.java)!!
                            addedBy.text = "Added by ${snapshot.getValue(String::class.java)!!}"

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                expenseCardView.setOnClickListener {
                    val intent = Intent(context, ExpenseDetails::class.java)
                    intent.putExtra("expenseId", expense.expenseId)
                    intent.putExtra("expenseName", expense.expenseName)
                    intent.putExtra("expenseDate", expense.dateOfExpense)
                    intent.putExtra("expensePaidBy",paidByName)
                    intent.putExtra("expenseAmount", expense.amount.toString())
                    expense.splitBetween?.let { it1 -> intent.putExtra("members", it1.size) }

                    context.startActivity(intent)
                }


            }

        }


    }
}