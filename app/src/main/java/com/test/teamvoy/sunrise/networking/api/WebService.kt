package com.test.teamvoy.sunrise.networking.api

import androidx.lifecycle.LiveData
import com.test.teamvoy.sunrise.networking.base.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    @GET("json")
    fun getSunriseTime(@Query("lat") latitude: Double, @Query("lng") longitude: Double)
            : LiveData<ApiResponse<SunriseResponse>>

}