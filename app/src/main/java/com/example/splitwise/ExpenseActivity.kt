package com.example.splitwise

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.addFriendAdapter
import com.example.splitwise.adapters.expenseAdapter
import com.example.splitwise.adapters.friendsPreviewAdapter
import com.example.splitwise.databinding.ActivityExpenseBinding
import com.example.splitwise.databinding.AddFriendInGroupBinding
import com.example.splitwise.models.BalanceAmountDetails
import com.example.splitwise.models.ExpenseModel
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseActivity : AppCompatActivity(), addFriendAdapter.ItemClickListener {
    val binding: ActivityExpenseBinding by lazy {
        ActivityExpenseBinding.inflate(layoutInflater)
    }
    val binding2: AddFriendInGroupBinding by lazy {
        AddFriendInGroupBinding.inflate(layoutInflater)
    }
    private lateinit var splitDialog: Dialog
    private val calender = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var expenseRef: DatabaseReference
    private var selectedGroupId: String = String()
    private var selectedGroupName: String = String()
    private var expenseName: String = String()
    private var dateOfExpense: String = String()
    private var expenseId: String = String()
    private var balanceId: String = String()
    private var paidByMemberId: String = String()
    private var expenseAmount: String = String()
    private var selectedMembersId: ArrayList<String> = arrayListOf()
    private var selectedMembersName: ArrayList<String> = arrayListOf()
    private var addMemberAdapter: addFriendAdapter? = null
    private var memberPreviewAdapter: friendsPreviewAdapter? = null
    private var allMemberDataList = ArrayList<UserModel>()
    private var allMemberIdList = ArrayList<String>()
    private var allMembersName = ArrayList<String>()
    private var mArrayAdapter: ArrayAdapter<String>? = null


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        expenseRef = Firebase.database.reference


//      For getting intent data-----------------------------------------------------------------------------------------------------------------------
        selectedGroupId = intent.getStringExtra("Id").toString()
        selectedGroupName = intent.getStringExtra("Name").toString()

//      For Spinner of Paid by Menu-----------------------------------------------------------------------------------------------------------------------

        getMembersId()
        mArrayAdapter = ArrayAdapter<String>(this, R.layout.spinner_list, allMembersName)
        mArrayAdapter!!.setDropDownViewResource(R.layout.spinner_list)
        binding.paidByMenu.setAdapter(mArrayAdapter)
        binding.paidByMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedData = parent?.getItemAtPosition(position) as String
                paidByMemberId = allMemberIdList.get(position)
            }

        }
//      For Dialog box of Radio Buttons---------------------------------------------------------------------------------------------------------------
        splitDialog = Dialog(this)
        splitDialog.setContentView(binding2.root)
        binding.splitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.splitEqually -> {
                    binding.expenseRecyclerView.visibility = View.GONE
                    memberPreviewAdapter?.notifyDataSetChanged()
                }

                R.id.splitInBetween -> {
                    splitDialog.show()
                    binding.expenseRecyclerView.visibility = View.VISIBLE
                    binding2.AddFriendButton.setOnClickListener {
                        setMemberPreviewAdapter()
                        splitDialog.dismiss()
                    }
                    allMemberIdList = selectedMembersId
                }

                else -> "None"
            }
        }


//      For Date Picker------------------------------------------------------------------------------------------------------------------------------------
        val date = Calendar.getInstance()
        date.set(
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        val initialFormatDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val initialFormattedDate = initialFormatDate.format(Date())
        binding.dateText.text = initialFormattedDate
        dateOfExpense = initialFormattedDate
        binding.dateText.setOnClickListener {
            openDateDialog()
        }
//      adding The Expense in Expense and balance database-----------------------------------------------------------------------------------------------------------------------
        binding.saveExpenseButton.setOnClickListener {
            expenseName = binding.expenseName.text.toString()
            expenseAmount = binding.expenseAmount.text.toString()
            if (expenseName.isNotEmpty() && expenseAmount.isNotEmpty() && dateOfExpense.isNotEmpty()) {
                addExpense(allMemberIdList)
//
                Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(
                    this@ExpenseActivity,
                    "All Fields are Required",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }


    private fun setMemberPreviewAdapter() {
        memberPreviewAdapter = friendsPreviewAdapter(selectedMembersName, this)
        binding.expenseRecyclerView.adapter = memberPreviewAdapter
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getMembersId() {
        val userId = auth.currentUser!!.uid
        auth = FirebaseAuth.getInstance()
        expenseRef =
            Firebase.database.reference.child("Groups").child(selectedGroupId).child("groupMembers")
        expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (memberId in snapshot.children) {
                    val member = memberId.getValue(String::class.java)
                    if (member != null) {
                        allMemberIdList.add(member)
                    }
                }
                getMembersData(allMemberIdList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExpenseActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private fun getMembersData(memberIdList: ArrayList<String>) {

        auth = FirebaseAuth.getInstance()
        var memberDataCount = memberIdList.size
        for (memberId in memberIdList) {
            expenseRef = Firebase.database.reference.child("Users").child(memberId)
            expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val memberData = snapshot.getValue(UserModel::class.java)

                    if (memberData != null) {
                        allMemberDataList.add(memberData)
                        allMembersName.add(memberData.userName.toString())
                        memberDataCount--

                    }
                    if (memberDataCount == 0) {
                        setAddFriendAdapter(allMemberDataList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ExpenseActivity, "Data not Retrieved", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }


    }

    private fun setAddFriendAdapter(friendsDataList: ArrayList<UserModel>) {

        addMemberAdapter = addFriendAdapter(friendsDataList, this, this)
        binding2.addFriendRecyclerView.adapter = addMemberAdapter
        binding2.addFriendRecyclerView.layoutManager = LinearLayoutManager(this)
        addMemberAdapter!!.notifyDataSetChanged()
    }

    private fun openDateDialog() {
        var datePickerDialog: Dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() { datePicker: DatePicker, year: Int, month: Int, date: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, date)
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.dateText.text = formattedDate
                Toast.makeText(this, "selectedDate = $formattedDate", Toast.LENGTH_SHORT).show()
                dateOfExpense = formattedDate
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onCheckedCheckbox(member: UserModel) {
        if (!selectedMembersId.contains(member.userId!!)) {
            selectedMembersId.add(member.userId!!)
            selectedMembersName.add(member.userName!!)
        }
    }

    override fun onUncheckedCheckbox(member: UserModel) {
        if (selectedMembersId.contains(member.userId!!)) {
            selectedMembersId.remove(member.userId!!)
            selectedMembersName.remove(member.userName!!)
        }
    }

    private fun addExpense(members: ArrayList<String>) {

        val userId = auth.currentUser?.uid
        val ref = Firebase.database.reference.child("Expenses")
        expenseId = ref.push().key!!
        val expense: ExpenseModel = ExpenseModel(
            expenseId=expenseId,
            createdBy = userId,
            expenseName = expenseName,
            amount = expenseAmount.toInt(),
            paidBy = paidByMemberId,
            splitBetween = members,
            dateOfExpense = dateOfExpense,
            groupId = selectedGroupId
        )
        ref.child(expenseId).setValue(expense)
        addBalanceAmountDetails()
    }

    private fun addBalanceAmountDetails() {
        val context = this@ExpenseActivity
        val expenseReference = Firebase.database.reference.child("Expenses").child(expenseId)
        if (expenseId.isNotEmpty()) {
            expenseReference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val expense = snapshot.getValue(ExpenseModel::class.java)
                        expense?.let {
                            val splitBetween = it.splitBetween
                            paidByMemberId = it.paidBy!!
                            if (splitBetween != null && it.amount != null) {
                                for (member in splitBetween) {
                                    val assignedAmount = (it.amount / splitBetween.size).toString()
                                    val balance = BalanceAmountDetails(
                                        owedBy = member,
                                        owedTo = paidByMemberId,
                                        amount = assignedAmount,
                                        groupId = it.groupId ?: "",
                                        expenseId = expenseId,
                                        expenseAmount = expenseAmount
                                    )
                                    balanceId = expenseRef.push().key ?: ""
                                    Firebase.database.reference.child("Balances").child(balanceId)
                                        .setValue(balance)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Expense ID is not initialized", Toast.LENGTH_SHORT).show()
        }
    }
}

