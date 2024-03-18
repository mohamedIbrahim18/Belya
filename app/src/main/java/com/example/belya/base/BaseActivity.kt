package com.example.belya.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Activity.showDialog(
    context: Context,
    message: String,
    posActionName: String?=null,
    posAction: DialogInterface.OnClickListener? = null,
    negActionName: String?=null,
    negAction: DialogInterface.OnClickListener? = null
): AlertDialog {
    val dialog = AlertDialog.Builder(context)
    dialog.setMessage(message)
    dialog.setPositiveButton(posActionName, posAction)
    dialog.setNegativeButton(negActionName, negAction)
    return dialog.show()
}