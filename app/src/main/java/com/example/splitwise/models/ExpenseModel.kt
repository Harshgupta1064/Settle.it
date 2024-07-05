package com.example.splitwise.models

import java.sql.Time
import java.util.Date

data class ExpenseModel(
    var expenseId:String?=null,
    var groupId:String?=null,
    var expenseName:String?=null,
    var amount:Int=0,
    var paidBy:String?=null,
    var createdBy:String?=null,
    var entryDate:Date?=null,
    var entryTime:Time?=null,
    var splitBetween:ArrayList<String>?=null,
    var dateOfExpense:String?=null,
)
