package com.test.teamvoy.sunrise.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.teamvoy.sunrise.networking.Repository

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        var error: MutableLiveData<String?> = MutableLiveData()
    }

    protected val repository = Repository()
}