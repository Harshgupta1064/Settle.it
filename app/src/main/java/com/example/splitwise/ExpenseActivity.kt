package com.example.splitwise

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splitwise.databinding.ActivityExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding: ActivityExpenseBinding by lazy {
            ActivityExpenseBinding.inflate(layoutInflater)
        }
        lateinit var splitDialog: Dialog
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

//      For Dialog box of Radio Buttons
        splitDialog = Dialog(this)
        splitDialog.setContentView(R.layout.add_friend)
        binding.splitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.splitEqually -> "Split Equally"
                R.id.splitInBetween -> splitDialog.show()
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
        binding.dateText.setOnClickListener{
            openDateDialog()
        }

    }

    private fun openDateDialog() {
        val date = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        val month = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
        val Year = SimpleDateFormat("YYYY", Locale.getDefault()).format(Date())
        var datePickerDialog: Dialog = DatePickerDialog(this,R.style.DatePicker,)
    }

}