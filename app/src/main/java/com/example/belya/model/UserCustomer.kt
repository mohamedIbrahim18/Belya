package com.example.belya.model

data class userCustomer(
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val phoneNumber : String?,
    val location : String?,
){
    constructor() : this("","","","","")
}
