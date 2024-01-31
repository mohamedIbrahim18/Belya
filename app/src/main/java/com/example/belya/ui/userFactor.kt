package com.example.belya.ui

data class userFactor (
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imagePath : String?,
    val phoneNumber : String?,
    val occupation : String?,
    val work_experience : String?
) {
    constructor() : this("","","","","","","")
}