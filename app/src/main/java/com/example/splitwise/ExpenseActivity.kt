package com.example.splitwise

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitwise.adapters.addFriendAdapter
import com.example.splitwise.databinding.ActivityExpenseBinding
import com.example.splitwise.databinding.AddFriendInGroupBinding
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

class ExpenseActivity : AppCompatActivity(),addFriendAdapter.ItemClickListener {
    val binding: ActivityExpenseBinding by lazy {
        ActivityExpenseBinding.inflate(layoutInflater)
    }
    val binding2: AddFriendInGroupBinding by lazy {
        AddFriendInGroupBinding.inflate(layoutInflater)
    }
    private lateinit var splitDialog: Dialog
    private val calender = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

//      For Dialog box of Radio Buttons
        splitDialog = Dialog(this)
        splitDialog.setContentView(binding2.root)
        getFriendIds()
        binding.splitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.splitEqually -> "Split Equally"
                R.id.splitInBetween -> {
                    splitDialog.show()
                }

                else -> "None"
            }
            Toast.makeText(this, "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
        }


//      For Spinner of Paid by Menu
        val mList = arrayOf<String?>("Harsh", "Prince", "Azad", "Shivam", "Nitin")
        val mArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_list, mList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.paidByMenu.adapter = mArrayAdapter

//      For Date Picker
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

    }

    private fun getFriendIds() {
        val userId = auth.currentUser!!.uid
        val friendsList = ArrayList<String>()
        auth = FirebaseAuth.getInstance()
        rootRef = Firebase.database.reference.child("Users").child(userId).child("friends")
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (friendId in snapshot.children) {
                    val friend = friendId.getValue(String::class.java)
                    if (friend != null) {
                        friendsList.add(friend)
                    }
                }
                getFriendsData(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExpenseActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getFriendsData(friendsList: ArrayList<String>) {
        val friendsDataList = ArrayList<UserModel>()
        val friendNames= ArrayList<String>()
        auth = FirebaseAuth.getInstance()
        for (friendId in friendsList) {
            rootRef = Firebase.database.reference.child("Users").child(friendId)
            rootRef.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friendData = snapshot.getValue(UserModel::class.java)
                    if (friendData != null) {
                        friendsDataList.add(friendData)
                    }
                    friendNames.add(friendData?.userName!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ExpenseActivity, "Data not Retrieved", Toast.LENGTH_SHORT).show()
                }
            })

        }
        setAddFriendAdapter(friendsDataList)



    }

    private fun setAddFriendAdapter(friendsDataList: ArrayList<UserModel>) {
        val adapter = addFriendAdapter(friendsDataList,this,this)
        binding2.addFriendRecyclerView.adapter = adapter
        binding2.addFriendRecyclerView.layoutManager = LinearLayoutManager(this)
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

    override fun onCheckedCheckbox(position: Int, friend: UserModel) {

    }

}