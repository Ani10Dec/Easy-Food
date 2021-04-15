package com.example.foodrunner.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class AppHelper {
    fun checkConnectivity(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null
    }
}