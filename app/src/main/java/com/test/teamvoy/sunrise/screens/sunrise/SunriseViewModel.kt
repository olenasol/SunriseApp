package com.test.teamvoy.sunrise.screens.sunrise

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.test.teamvoy.sunrise.R
import com.test.teamvoy.sunrise.base.BaseViewModel
import com.test.teamvoy.sunrise.networking.base.ScreenState
import com.test.teamvoy.sunrise.base.SunriseApp
import com.test.teamvoy.sunrise.networking.base.Resource
import com.test.teamvoy.sunrise.networking.base.State
import com.test.teamvoy.sunrise.networking.api.SunriseResponse


class SunriseViewModel(application: Application):BaseViewModel(application){

    lateinit var googleApiClient:GoogleApiClient

    private val screenState = MutableLiveData<ScreenState>()



    fun getScreenState() = screenState as LiveData<ScreenState>
    init {
        initApiClient()
    }
    private fun initApiClient(){
        googleApiClient = GoogleApiClient.Builder(getApplication<SunriseApp>().applicationContext)
            .addApi(LocationServices.API)
            .addOnConnectionFailedListener {
                error.postValue(getApplication<SunriseApp>().applicationContext.getString(R.string.failed_get_location))
            }
            .build()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentSunriseTime(){
        val fusedLocationApi = LocationServices.getFusedLocationProviderClient(getApplication<SunriseApp>().applicationContext)
        fusedLocationApi.lastLocation.addOnSuccessListener {
            location ->
                location?.let {
                    val sunriseResponse = repository.getSunriseTime(it.latitude,it.longitude)
                    sunriseResponse.observeForever(object : Observer<Resource<SunriseResponse>> {
                        override fun onChanged(resource: Resource<SunriseResponse>?) {
                            if (resource != null) {
                                when (resource.state) {
                                    State.LOADING->screenState.postValue(ScreenState.LOADING)
                                    State.FAILURE->{
                                        screenState.postValue(ScreenState.ERROR)
                                        error.postValue(resource.message)
                                        sunriseResponse.removeObserver(this)
                                    }
                                    State.SUCCESS->{
                                        screenState.postValue(ScreenState.SUCCESS)
                                        Log.d("TEST",resource.data.toString())
                                        sunriseResponse.removeObserver(this)
                                    }
                                }
                            }
                        }

                    })
                }

        }
    }

    fun onPause() {
        googleApiClient.disconnect()
    }

    fun onResume() {
        googleApiClient.connect()
    }
}