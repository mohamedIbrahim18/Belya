package com.example.belya.model
// Delete this file
// replace this file with (UserTechnician)
data class PersonItem (
    val  imagePath: String,
    val firstName :String,
    val job : String,
    val person_rate : Double
){
    constructor() : this("","","",0.0)
}