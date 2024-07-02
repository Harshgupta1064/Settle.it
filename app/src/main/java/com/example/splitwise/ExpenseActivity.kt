package com.example.splitwise

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.addFriendAdapter
import com.example.splitwise.adapters.friendsPreviewAdapter
import com.example.splitwise.databinding.ActivityExpenseBinding
import com.example.splitwise.databinding.AddFriendInGroupBinding
import com.example.splitwise.models.GroupModel
import com.example.splitwise.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import org.jetbrains.annotations.Async
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("IMPLICIT_CAST_TO_ANY")
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
    private var expenseAmount: String = String()
    private var paidByMemberPosition: Int? = null
    private var amountToEachMember: Float? = null
    private var selectedMembersId: ArrayList<String> = arrayListOf()
    private var selectedMembersName: ArrayList<String> = arrayListOf()
    private var amountToSelectedMember: Float? = null
    private var groupDetails: GroupModel?=null
    private var groupMenbers:Array<String> = arrayOf()
    private var addMemberAdapter: addFriendAdapter? = null
    private var memberPreviewAdapter: friendsPreviewAdapter? = null
    private var allMemberDataList = ArrayList<UserModel>()
    private var allMembersName = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

//      Getting User Data-----------------------------------------------------------------------------------------------------------------------
        expenseName = binding.expenseName.text.toString()
        expenseAmount = binding.expenseAmount.text.toString()
//      For getting intent data-----------------------------------------------------------------------------------------------------------------------
        selectedGroupId = intent.getStringExtra("groupId").toString()
        selectedGroupName = intent.getStringExtra("groupName").toString()

//      For Spinner of Paid by Menu-----------------------------------------------------------------------------------------------------------------------
        getMembersId()
        val mArrayAdapter = ArrayAdapter<String>(this, R.layout.spinner_list,
            allMembersName as List<String>
        )
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.paidByMenu.adapter = mArrayAdapter
        paidByMemberPosition = binding.paidByMenu.selectedItemPosition
//      For Dialog box of Radio Buttons---------------------------------------------------------------------------------------------------------------
        splitDialog = Dialog(this)
        splitDialog.setContentView(binding2.root)
        binding.splitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.splitEqually -> {
                    selectedMembersId.clear()
                    selectedMembersName.clear()
                    memberPreviewAdapter!!.notifyDataSetChanged()

                }
//                    val noOfMembers: Int = groupDetails.groupMembers!!.size
//                    amountToEachMember =
//                        (expenseAmount.toInt()) / (noOfMembers.toBigInteger()).toFloat()


                R.id.splitInBetween -> {
                    splitDialog.show()
                    binding2.AddFriendButton.setOnClickListener {
                        setMemberPreviewAdapter()
                        splitDialog.dismiss()
                    }
                }

                else -> "None"
            }
            Toast.makeText(this, "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
        }



//      For Date Picker------------------------------------------------------------------------------------------------------------------------------------
        val date = Calendar.getInstance()
        date.set(
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        val formatDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = formatDate.format(Date())
        binding.dateText.text = formattedDate
        binding.dateText.setOnClickListener {
            openDateDialog()
        }
//      For getting MembersIds-----------------------------------------------------------------------------------------------------------------------


    }

    private fun setMemberPreviewAdapter() {
        memberPreviewAdapter = friendsPreviewAdapter(selectedMembersName, this)
        binding.expenseRecyclerView.adapter = memberPreviewAdapter
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getMembersId() {
        val userId = auth.currentUser!!.uid
        val membersIdList = ArrayList<String>()
        auth = FirebaseAuth.getInstance()
        expenseRef =
            Firebase.database.reference.child("Groups").child(selectedGroupId).child("groupMembers")
        expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (memberId in snapshot.children) {
                    val member = memberId.getValue(String::class.java)
                    if (member != null) {
                        membersIdList.add(member)
                    }
                }
                getMembersData(membersIdList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExpenseActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private fun getMembersData(memberIdList: ArrayList<String>) {

        auth = FirebaseAuth.getInstance()
        for (memberId in memberIdList) {
            expenseRef = Firebase.database.reference.child("Users").child(memberId)
            expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val memberData = snapshot.getValue(UserModel::class.java)

                    if (memberData != null) {
                        allMemberDataList.add(memberData)
                        allMembersName.add(memberData.userName.toString())

                    }
                    if (memberId == memberIdList.last()) {
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
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onCheckedCheckbox( member: UserModel) {
        if (!selectedMembersId!!.contains(member.userId!!)) {
            selectedMembersId!!.add(member.userId!!)
            selectedMembersName!!.add(member.userName!!)
        }
    }

}