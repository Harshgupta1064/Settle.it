package com.example.splitwise.models

data class FriendModel(
    var friendId: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var usersOwedMoneyFromFriend:Int=0,
    var userOwesMoneyToFriend:Int=0,
    var expenses:ArrayList<ExpenseModel>?=null,
    var commonGroups:ArrayList<GroupModel>?=null
)
