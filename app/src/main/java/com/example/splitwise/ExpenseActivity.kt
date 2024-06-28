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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {
    val binding: ActivityExpenseBinding by lazy {
        ActivityExpenseBinding.inflate(layoutInflater)
    }
    lateinit var splitDialog: Dialog
    private val calander = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

//      For Dialog box of Radio Buttons
        splitDialog = Dialog(this)
        splitDialog.setContentView(R.layout.add_friend_in_group)
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
        val date = Calendar.getInstance()
        date.set(calander.get(Calendar.YEAR),calander.get(Calendar.MONTH),calander.get(Calendar.DAY_OF_MONTH))
        val formatDate = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        val formattedDate = formatDate.format(Date())
        binding.dateText.text = formattedDate
        binding.dateText.setOnClickListener {
            openDateDialog()
        }

    }

    private fun openDateDialog() {
        var datePickerDialog: Dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener(){ datePicker:DatePicker, year: Int, month: Int, date: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year,month,date)
                val dateFormat = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.dateText.text = formattedDate
            },
            calander.get(Calendar.YEAR),
            calander.get(Calendar.MONTH),
            calander.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

}