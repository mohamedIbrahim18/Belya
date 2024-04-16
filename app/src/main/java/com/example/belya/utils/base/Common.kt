package com.example.belya.utils.base

import android.content.Context
import android.net.ConnectivityManager
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import com.example.belya.R

class Common {
    fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val isConnected = cm?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        if (!isConnected) {
            showInternetDisconnectedDialog(context)
        }
        return isConnected
    }

    fun showInternetDisconnectedDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.check_internet_dialog, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val reloadButton = dialogView.findViewById<Button>(R.id.rety)
        reloadButton.setOnClickListener {
           if( isConnectedToInternet(context)){
               dialog.dismiss()
           }
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}
