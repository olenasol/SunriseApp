package com.test.teamvoy.sunrise.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.test.teamvoy.sunrise.MainActivity.Companion.ACTION_CONNECTION_AVAILABLE
import com.test.teamvoy.sunrise.MainActivity.Companion.ACTION_CONNECTION_LOST
import com.test.teamvoy.sunrise.R

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    protected lateinit var viewModel: VM

    private val onConnectionLostReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onNetworkConnectionLost()
        }
    }
    private val onConnectionAvailableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onNetworkConnectionAvailable()
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initUi(view: View?)

    // region observers
    private val errorObserver: androidx.lifecycle.Observer<String?> = androidx.lifecycle.Observer { s: String? ->
        if (s != null) {
            showErrorAlerter(s)
            BaseViewModel.error.postValue(null)
        }
    }

    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            initUi(getView())
        }
        BaseViewModel.error.removeObserver(errorObserver)
        BaseViewModel.error.observe(this, errorObserver)


    }

    override fun onResume() {
        super.onResume()
        val connectionLostFilter = IntentFilter(ACTION_CONNECTION_LOST)
        val connectionAvailableFilter = IntentFilter(ACTION_CONNECTION_AVAILABLE)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(onConnectionLostReceiver, connectionLostFilter)
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(onConnectionAvailableReceiver, connectionAvailableFilter)
        }
    }

    override fun onPause(){
        super.onPause()
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(onConnectionLostReceiver)
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(onConnectionAvailableReceiver)
        }
    }

    protected fun showErrorAlerter(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
    protected fun onNetworkConnectionLost() {
        context?.let {
            showErrorAlerter(it.getString(R.string.no_internet))
        }
    }

    protected fun onNetworkConnectionAvailable() {
    }
}