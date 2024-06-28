package com.example.splitwise.models

data class UserModel(
    var userId:String?=null,
    var userName:String?=null,
    var email:String?=null,
    var password:String?=null,
    var phoneNumber:String?=null,
    var owedMoney:Int = 0,
    var oweMoney:Int = 0,
    var balance:Int = 0,
    var friends:ArrayList<UserModel>?=null,
    var groups:ArrayList<GroupModel>?=null,
){

}