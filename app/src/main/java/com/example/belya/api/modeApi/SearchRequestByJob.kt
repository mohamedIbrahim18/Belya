package com.example.belya.api.modeApi

import com.google.gson.annotations.SerializedName

data class SearchRequestByJob(
    @SerializedName("job")
    val job: String
)