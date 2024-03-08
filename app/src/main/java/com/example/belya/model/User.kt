package com.example.belya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imagePath : String?,
    val phoneNumber : String?,
    val city : String?,
    val job : String?,
    val work_experience : String?,
    val person_rate : Double,
    val userType :String,
    var userID : String,
    var price : String,
    var acceptedList : List<String>,
    var pendingList : List<String>,
) : Parcelable
{
constructor() : this("","","","","","","","",
    0.0,"","","", emptyList(),emptyList())
}
