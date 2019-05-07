package com.test.teamvoy.sunrise.networking.api

import com.google.gson.annotations.SerializedName

data class SunriseResponse(
    val results: ResultData?, val status:String?
)

data class ResultData(
    @field:SerializedName("sunrise") val sunriseTime: String,
    @field:SerializedName("sunset") val sunsetTime:String
)
data class APIError(
    val status:String?
)
