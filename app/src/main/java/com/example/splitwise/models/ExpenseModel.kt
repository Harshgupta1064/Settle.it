package com.example.splitwise.models

data class ExpenseModel(
    var expenseId:String?=null,
    var expenseName:String?=null,
    var amount:Int=0,
    var paidBy:String?=null,
    var splitBetween:ArrayList<UserModel>?=null,
    var dateOfExpense:String?=null,
)
