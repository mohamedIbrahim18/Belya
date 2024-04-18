package com.example.belya.api.modeApi

import com.google.gson.annotations.SerializedName

data class UserApiModelItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("imagePath")
	val imagePath: String? = null,

	@field:SerializedName("person_rate")
	val personRate: Int? = null,

	@field:SerializedName("pendingList")
	val pendingList: List<Any?>? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("acceptedList")
	val acceptedList: List<Any?>? = null,

	@field:SerializedName("userType")
	val userType: String? = null,

	@field:SerializedName("job")
	val job: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("work_experience")
	val workExperience: String? = null
)