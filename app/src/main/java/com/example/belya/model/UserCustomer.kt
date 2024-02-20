package com.example.belya.model

data class UserCustomer (
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imagePath : String?,
    val phoneNumber : String?,
    val city : String?,
){
    constructor():this("","","","","","")
}