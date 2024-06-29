package com.example.splitwise.models

data class GroupModel (
    var groupId:String?=null,
    var groupName:String?=null,
    var groupMembers:ArrayList<String>?=null,
)