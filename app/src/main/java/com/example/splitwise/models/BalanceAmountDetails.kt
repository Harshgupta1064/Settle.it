package com.example.splitwise.models

data class BalanceAmountDetails(
    val expenseId: String? = null,
    val groupId: String? = null,
    val expenseAmount: String? = null,
    var owedBy: String? = null,
    var owedTo: String? = null,
    val amount: String? = null,

    )
