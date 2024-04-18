package com.example.belya.api.modeApi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize

data class UserApiModel(
	@field:SerializedName("UserApiModel")
	val userApiModel: List<UserApiModelItem?>? = null
): Parcelable