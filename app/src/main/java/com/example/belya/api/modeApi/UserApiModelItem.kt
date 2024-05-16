package com.example.belya.api.modeApi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class UserApiModelItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("imagePath")
	val imagePath: String? = null,

	@field:SerializedName("person_rate")
	val personRate: Double? = null,

	@field:SerializedName("pendingList")
	val pendingList: @RawValue List<Any?>? = null, // Use @RawValue here

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("acceptedList")
	val acceptedList: @RawValue List<Any?>? = null,

	@field:SerializedName("userType")
	val userType: String? = null,

	@field:SerializedName("job")
	val job: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("work_experience")
	val workExperience: String? = null
): Parcelable