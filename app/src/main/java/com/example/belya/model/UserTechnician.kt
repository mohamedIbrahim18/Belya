package com.example.belya.model

data class UserTechnician (

    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imagePath : String?,
    val phoneNumber : String?,
    val city : String?,
    val job : String?,
    val work_experience : String?,
    val person_rate : Double,
){
    companion object{
        const val UserType="Technician"
    }
    constructor(): this("","","","","","","","",0.0)
}