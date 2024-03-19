package com.example.belya.utils.base

import android.app.Activity
import android.app.AlertDialog
import com.example.belya.R

class LoadingDialog(val mActivity: Activity){
    private lateinit var isDialog:AlertDialog
    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog_layout,null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }
    fun isDismiss(){
        isDialog.dismiss()
    }
}