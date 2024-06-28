package com.example.splitwise.models

data class GroupModel (
    var groupId:String?=null,
    var groupName:String?=null,
    var balanceAmountToEachFriend:ArrayList<Pair<FriendModel,Int>>?=null,
    var groupMembers:ArrayList<UserModel>?=null,
    var expenses:ArrayList<ExpenseModel>?=null,
)