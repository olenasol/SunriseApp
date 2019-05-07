package com.test.teamvoy.sunrise.networking

import androidx.lifecycle.LiveData
import com.test.teamvoy.sunrise.networking.api.ApiClient
import com.test.teamvoy.sunrise.networking.api.CitiesResponse
import com.test.teamvoy.sunrise.networking.api.SunriseResponse
import com.test.teamvoy.sunrise.networking.base.ApiResponse
import com.test.teamvoy.sunrise.networking.base.NetworkBoundResource
import com.test.teamvoy.sunrise.networking.base.Resource

class Repository {
    fun getSunriseTime(latitude:Double, longitude:Double): LiveData<Resource<SunriseResponse>> {
        return object : NetworkBoundResource<SunriseResponse>() {
            override fun saveCallResult(item: SunriseResponse) {
            }

            override fun createCall(): LiveData<ApiResponse<SunriseResponse>> {
                return ApiClient.getClient().webService.getSunriseTime(latitude,longitude)
            }
        }.asLiveData()
    }
//    fun getCities(input:String):LiveData<Resource<CitiesResponse>>{
//        return object : NetworkBoundResource<CitiesResponse>(){
//            override fun saveCallResult(item: CitiesResponse) {
//
//            }
//
//            override fun createCall(): LiveData<ApiResponse<CitiesResponse>> {
//                return GoogleApiClient.getClient().webService.getListOfCities(input)
//            }
//
//        }.asLiveData()
//    }
}