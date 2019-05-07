package com.test.teamvoy.sunrise

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.libraries.places.api.Places
import com.test.teamvoy.sunrise.base.SunriseApp
import com.test.teamvoy.sunrise.screens.sunrise.SunriseFragment
import com.test.teamvoy.sunrise.networking.NetworkStateReceiver
import com.test.teamvoy.sunrise.util.replaceFragment

class MainActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener  {

    companion object {
        const val API_KEY = "AIzaSyClqTH0S_uxcDupORgNWzW7MmSeLewF19Y"
        const val ACTION_CONNECTION_LOST = "action_internet_connection_lost"
        const val ACTION_CONNECTION_AVAILABLE = "action_internet_connection_available"
    }
    private lateinit var networkStateReceiver: NetworkStateReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(SunriseFragment.newInstance(),R.id.mainContent)
        Places.initialize(applicationContext, API_KEY)
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun networkAvailable() {
        SunriseApp.setNetworkAvailable(true)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intent = Intent(ACTION_CONNECTION_AVAILABLE)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun networkUnavailable() {
        SunriseApp.setNetworkAvailable(false)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intent = Intent(ACTION_CONNECTION_LOST)
        localBroadcastManager.sendBroadcast(intent)
    }
}
