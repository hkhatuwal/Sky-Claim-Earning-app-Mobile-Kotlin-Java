package com.sgamerapps.android.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager


class Helper {

    companion object{


        fun  isConnectingToInternet(context: Context):Boolean{
//            return if (isOnline(context)) {
//                try {
//                    val p1 = Runtime.getRuntime().exec(
//                        "ping -c 1 www.google.com"
//                    )
//                    val returnVal = p1.waitFor()
//                    val reachable = returnVal == 0
//                    if (reachable) {
//                        println("Internet access")
//                        reachable
//                    } else {
//                        false
//                    }
//                } catch (e: Exception) {
//                    false
//                }
//            } else false
            return isOnline(context)
        }
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            }
            return false
        }
        fun hideKeyboard(context: Context,view: View) {
            try {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (ignored: java.lang.Exception) {
            }
        }

         fun copyTextToClipboard(context: Context,text:String) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Code", text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }
}