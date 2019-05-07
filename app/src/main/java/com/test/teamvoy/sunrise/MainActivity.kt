package com.test.teamvoy.sunrise

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.test.teamvoy.sunrise.base.SunriseApp
import com.test.teamvoy.sunrise.networking.NetworkStateReceiver
import com.test.teamvoy.sunrise.screens.sunrise.SunriseFragment
import com.test.teamvoy.sunrise.util.replaceFragment

class MainActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    companion object {
        const val ACTION_CONNECTION_LOST = "action_internet_connection_lost"
        const val ACTION_CONNECTION_AVAILABLE = "action_internet_connection_available"
    }

    private lateinit var networkStateReceiver: NetworkStateReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(SunriseFragment.newInstance(), R.id.mainContent)
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
