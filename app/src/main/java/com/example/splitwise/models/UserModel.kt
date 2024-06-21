package com.example.splitwise.models

class UserModel(
    userName:String,
    email:String,
    password:String,
    phoneNumber:Int,
    owedMoney:Int,
    oweMoney:Int,
    balance:Int,
    friends:ArrayList<UserModel>,
    groups:ArrayList<GroupModel>,
){

}