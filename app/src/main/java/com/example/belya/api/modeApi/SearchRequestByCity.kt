package com.example.belya.api.modeApi

import com.google.gson.annotations.SerializedName

data class SearchRequestByCity(
    @SerializedName("city")
    val city: String,

    @SerializedName("category")
    val category: String
)