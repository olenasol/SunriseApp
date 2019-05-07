package com.test.teamvoy.sunrise.networking

import androidx.lifecycle.LiveData
import com.test.teamvoy.sunrise.networking.api.ApiClient
import com.test.teamvoy.sunrise.networking.api.SunriseResponse
import com.test.teamvoy.sunrise.networking.base.ApiResponse
import com.test.teamvoy.sunrise.networking.base.NetworkBoundResource
import com.test.teamvoy.sunrise.networking.base.Resource

class Repository {

    fun getSunriseTime(latitude: Double, longitude: Double): LiveData<Resource<SunriseResponse>> {
        return object : NetworkBoundResource<SunriseResponse>() {

            override fun createCall(): LiveData<ApiResponse<SunriseResponse>> {
                return ApiClient.getClient().webService.getSunriseTime(latitude, longitude)
            }
        }.asLiveData()
    }

}