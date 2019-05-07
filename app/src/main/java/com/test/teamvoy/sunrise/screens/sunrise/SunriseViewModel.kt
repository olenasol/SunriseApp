package com.test.teamvoy.sunrise.screens.sunrise

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.test.teamvoy.sunrise.R
import com.test.teamvoy.sunrise.base.BaseViewModel
import com.test.teamvoy.sunrise.base.SunriseApp
import com.test.teamvoy.sunrise.networking.api.SunriseResponse
import com.test.teamvoy.sunrise.networking.base.Resource
import com.test.teamvoy.sunrise.networking.base.ScreenState
import com.test.teamvoy.sunrise.networking.base.State
import java.util.*

class SunriseViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var placesClient: PlacesClient

    private val currentLocationStateLiveData = MutableLiveData<ScreenState>()
    private val currentLocationLiveData = MutableLiveData<SunriseResponse?>()

    private val citySunriseStateLiveData = MutableLiveData<ScreenState>()
    private val citySunriseLiveData = MutableLiveData<SunriseResponse?>()

    fun getCurrentLocationState() = currentLocationStateLiveData as LiveData<ScreenState>
    fun getCurrentLocation() = currentLocationLiveData as LiveData<SunriseResponse?>

    fun getCitySunriseState() = citySunriseStateLiveData as LiveData<ScreenState>
    fun getCitySunrise() = citySunriseLiveData as LiveData<SunriseResponse?>

    private var sunriseResponse : LiveData<Resource<SunriseResponse>> = MutableLiveData()

    init {
        initApiClient()
    }
    //region Observers
    private val currentTimeSunriseObserver:Observer<Resource<SunriseResponse>> = object : Observer<Resource<SunriseResponse>> {
        override fun onChanged(resource: Resource<SunriseResponse>?) {
            if (resource != null) {
                when (resource.state) {
                    State.LOADING -> {
                        currentLocationStateLiveData.postValue(ScreenState.LOADING)
                    }
                    State.FAILURE -> {
                        currentLocationStateLiveData.postValue(ScreenState.ERROR)
                        error.postValue(resource.message)
                        currentLocationLiveData.postValue(null)
                        sunriseResponse.removeObserver(this)
                    }
                    State.SUCCESS -> {
                        currentLocationStateLiveData.postValue(ScreenState.SUCCESS)
                        currentLocationLiveData.postValue(resource.data)
                        sunriseResponse.removeObserver(this)
                    }
                }
            }
        }
    }
    private val cityTimeSunriseObserver:Observer<Resource<SunriseResponse>> = object : Observer<Resource<SunriseResponse>> {
        override fun onChanged(resource: Resource<SunriseResponse>?) {
            if (resource != null) {
                when (resource.state) {
                    State.LOADING -> {
                        citySunriseStateLiveData.postValue(ScreenState.LOADING)
                        citySunriseLiveData.postValue(null)
                    }
                    State.FAILURE -> {
                        citySunriseStateLiveData.postValue(ScreenState.ERROR)
                        error.postValue(resource.message)
                        citySunriseLiveData.postValue(null)
                        sunriseResponse.removeObserver(this)
                    }
                    State.SUCCESS -> {
                        citySunriseStateLiveData.postValue(ScreenState.SUCCESS)
                        citySunriseLiveData.postValue(resource.data)
                        sunriseResponse.removeObserver(this)
                    }
                }
            }
        }
    }
    //endregion

    private fun initApiClient() {
        placesClient = Places.createClient(getApplication<SunriseApp>().applicationContext)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentSunriseTime() {
        currentLocationLiveData.postValue(null)
        val placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        currentLocationStateLiveData.postValue(ScreenState.LOADING)
        val placeResponse = placesClient.findCurrentPlace(request)
        placeResponse.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                getSunriseForLocation(
                    response?.placeLikelihoods?.get(0)?.place?.latLng?.latitude,
                    response?.placeLikelihoods?.get(0)?.place?.latLng?.longitude,
                    null
                )
            } else {
                currentLocationLiveData.postValue(null)
                currentLocationStateLiveData.postValue(ScreenState.ERROR)
                error.postValue(getApplication<SunriseApp>().applicationContext.getString(R.string.failed_get_location))
            }
        }
    }

    private fun getSunriseForLocation(latitude: Double?, longitude: Double?,name:String?) {
        if (latitude != null && longitude != null) {
            sunriseResponse = repository.getSunriseTime(latitude, longitude)
            if (name == null)
                sunriseResponse.observeForever(currentTimeSunriseObserver)
            else
                sunriseResponse.observeForever(cityTimeSunriseObserver)
        } else {
            error.postValue(getApplication<SunriseApp>().applicationContext.getString(R.string.failed_get_location))
            if (name == null)
                currentLocationLiveData.postValue(null)
            else
                citySunriseLiveData.postValue(null)
        }
    }

    fun onPlaceSelected(place: Place) {
        getSunriseForLocation(place.latLng?.latitude,place.latLng?.longitude,place.name)
    }

}