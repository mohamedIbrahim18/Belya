package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.who_in_this_category_adapter
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