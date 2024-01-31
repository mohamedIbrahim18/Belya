package com.example.belya.ui

data class userCustomer(
    val firstName : String?,
    val lastName : String?,
    val email : String?,
){
    constructor() : this("","","")
}
