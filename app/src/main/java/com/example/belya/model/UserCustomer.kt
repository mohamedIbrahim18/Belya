package com.example.belya.model

data class userCustomer(
    val firstName : String?,
    val lastName : String?,
    val email : String?,
){
    constructor() : this("","","")
}
