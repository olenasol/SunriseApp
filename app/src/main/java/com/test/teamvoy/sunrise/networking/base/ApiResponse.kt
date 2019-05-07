package com.test.teamvoy.sunrise.networking.base

import com.test.teamvoy.sunrise.networking.api.APIError
import com.test.teamvoy.sunrise.networking.api.ApiClient
import retrofit2.Response
import java.io.IOException
import java.util.*

class ApiResponse<D> {

    private val data: D?

    private val error: Throwable?

    val errorMessage: String?

    val statusCode: Int?

    val isSuccessful: Boolean
        get() = data != null && error == null

    constructor(Data: D) {
        Objects.requireNonNull(Data)
        this.data = Data
        this.error = null
        this.errorMessage = null
        this.statusCode = null
    }

    constructor(response: Response<D>) {
        this.data = response.body()
        this.error = null
        if (!response.isSuccessful) {
            this.statusCode = response.code()
            this.errorMessage = parseError(response)!!.status
        } else {
            this.statusCode = response.code()
            this.errorMessage = null
        }
    }

    constructor(error: Throwable) {
        Objects.requireNonNull(error)

        this.data = null
        this.error = error
        this.errorMessage = null
        this.statusCode = null
    }

    fun getData(): D {
        if (data == null) {
            throw IllegalStateException("Data is null")
        }
        return data
    }

    fun getError(): Throwable {
        if (error == null) {
            throw IllegalStateException("Error is null")
        }
        return error
    }

    fun parseError(response: Response<*>): APIError? {
        val converter = ApiClient.getClient().retrofit
            .responseBodyConverter<APIError>(APIError::class.java, arrayOfNulls(0))

        val error: APIError?

        try {
            error = converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            return APIError(null)
        }

        return error
    }
}

