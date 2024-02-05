package com.example.belya.model

import android.os.Parcel
import android.os.Parcelable

data class userTechnician (
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imagePath : String?,
    val phoneNumber : String?,
    val job : String?,
    val work_experience : String?,
    val person_rate : Double,
    val city : String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    constructor() : this("","","","",
        "","","",0.0,"")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(imagePath)
        parcel.writeString(phoneNumber)
        parcel.writeString(job)
        parcel.writeString(work_experience)
        parcel.writeDouble(person_rate)
        parcel.writeString(city)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<userTechnician> {
        override fun createFromParcel(parcel: Parcel): userTechnician {
            return userTechnician(parcel)
        }

        override fun newArray(size: Int): Array<userTechnician?> {
            return arrayOfNulls(size)
        }
    }
}