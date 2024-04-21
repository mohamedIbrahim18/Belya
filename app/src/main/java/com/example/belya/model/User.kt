package com.example.belya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var firstName : String?,
    var lastName : String?,
    val email : String?,
    val imagePath : String?,
    var phoneNumber : String?,
    val city : String?,
    val job : String?,
    val work_experience : String?,
    val person_rate : Double,
    val userType :String,
    var userID : String,
    var price : String,
    var acceptedList : List<String>,
    var pendingList : List<String>,
    var description :String
) : Parcelable
{
constructor() : this("","","","","","","","",
    0.0,"","","", emptyList(),emptyList(),"")
}
