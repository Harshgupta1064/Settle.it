package com.example.splitwise.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.ExpenseDetails
import com.example.splitwise.adapters.expenseAdapter.ExpenseViewHolder
import com.example.splitwise.databinding.ExpenseItemBinding
import com.example.splitwise.models.BalanceAmountDetails
import com.example.splitwise.models.ExpenseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsExpenseAdapter(
    private val balanceList: ArrayList<BalanceAmountDetails>,
    private val context: Context
) : RecyclerView.Adapter<FriendsExpenseAdapter.friendsExpenseViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendsExpenseViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return friendsExpenseViewHolder(binding)
    }


    override fun onBindViewHolder(holder: friendsExpenseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
            return balanceList.size
    }
    inner class friendsExpenseViewHolder(private val binding: ExpenseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            var balance = balanceList[position]
            var expenseId = balance.expenseId
            var friendId: String = String()
            rootRef.child("Expenses").child(expenseId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val expense = snapshot.getValue(ExpenseModel::class.java)
                        if (expense != null) {
                            if(auth.currentUser!!.uid==expense.paidBy){
                                friendId=balance.owedTo!!
                            }
                            else{
                                friendId=balance.owedBy!!
                            }
                            binding.apply {
                                expenseName.text = expense.expenseName
                                expenseAmount.text = expense.amount.toString()
                                dateOfExpense.text = expense.dateOfExpense
                                rootRef.child("Users").child(expense.createdBy!!).child("userName")
                                    .addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            addedBy.text =
                                                "Added by ${snapshot.getValue(String::class.java)!!}"
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                context,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                                binding.expenseCardView.setOnClickListener{

                                    val intent = Intent(context, ExpenseDetails::class.java)
                                    intent.putExtra("expenseId", balance.expenseId)
                                    intent.putExtra("expenseName", expense.expenseName)
                                    intent.putExtra("expenseDate", expense.dateOfExpense)
                                    intent.putExtra("expensePaidBy", balance.owedTo)
                                    intent.putExtra("expenseOwedBy", balance.owedBy)
                                    intent.putExtra("owedAmount", balance.amount.toString())
                                    intent.putExtra("expenseAmount", balance.expenseAmount.toString())

                                    context.startActivity(intent)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

        }
        }
    }