package com.test.teamvoy.sunrise.base

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.test.teamvoy.sunrise.networking.base.AppExecutors

class SunriseApp:Application(){

    companion object{
        private var networkAvailable: Boolean = false

        fun isNetworkAvailable():Boolean{
            return networkAvailable
        }

        fun setNetworkAvailable(isAvailable:Boolean){
            networkAvailable = isAvailable
        }
    }
    override fun onCreate() {
        super.onCreate()

        AppExecutors()
        networkAvailable = getNetworkState()
    }
    private fun getNetworkState(): Boolean {
        val conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = conMgr.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
    }

}