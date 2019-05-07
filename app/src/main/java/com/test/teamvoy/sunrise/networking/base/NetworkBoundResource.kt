package com.test.teamvoy.sunrise.networking.base

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.test.teamvoy.sunrise.base.SunriseApp

abstract class NetworkBoundResource<ResultType> @MainThread
constructor() {

    private val result = MutableLiveData<Resource<ResultType>>()

    init {
        if (SunriseApp.isNetworkAvailable()) {
            result.postValue(Resource.loading(null))
            fetchFromNetwork()
        } else {
            result.postValue(
                Resource.error(
                    "no internet connection",
                    null,
                    null
                )
            )
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        apiResponse.observeForever(object : Observer<ApiResponse<ResultType>> {
            override fun onChanged(resultTypeApiResponse: ApiResponse<ResultType>?) {
                if (resultTypeApiResponse != null && resultTypeApiResponse.isSuccessful) {
                    AppExecutors.diskIO()!!.execute {
                        saveCallResult(processResponse(resultTypeApiResponse))
                        AppExecutors.mainThread()!!.execute { result.setValue(
                            Resource.success(
                                resultTypeApiResponse.getData()
                            )
                        ) }
                    }
                } else {
                    if (resultTypeApiResponse != null && resultTypeApiResponse.errorMessage != null) {
                        result.setValue(
                            Resource.error(
                                resultTypeApiResponse.errorMessage,
                                null,
                                resultTypeApiResponse.statusCode
                            )
                        )
                    } else if (resultTypeApiResponse != null) {
                        result.value = Resource.error(
                            resultTypeApiResponse.getError().message,
                            null,
                            resultTypeApiResponse.statusCode
                        )
                    }
                }
                apiResponse.removeObserver(this)
            }
        })
    }

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    private fun processResponse(response: ApiResponse<ResultType>): ResultType {
        return response.getData()
    }

    @WorkerThread
    protected fun saveCallResult(item: ResultType){

    }

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>
}
