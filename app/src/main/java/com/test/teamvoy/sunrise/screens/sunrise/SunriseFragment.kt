package com.test.teamvoy.sunrise.screens.sunrise

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.test.teamvoy.sunrise.R
import com.test.teamvoy.sunrise.base.BaseFragment
import com.test.teamvoy.sunrise.networking.api.SunriseResponse
import com.test.teamvoy.sunrise.networking.base.ScreenState
import com.test.teamvoy.sunrise.util.getTimeStringFromUtcString
import com.test.teamvoy.sunrise.util.observeSafe
import kotlinx.android.synthetic.main.fragment_sunrise.*
import java.util.Arrays.asList


class SunriseFragment : BaseFragment<SunriseViewModel>() {

    companion object {
        const val PERMISSION_LOCATION_REQUEST = 123

        fun newInstance(): SunriseFragment {
            return SunriseFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sunrise
    }

    //region Observers
    private val currentLoadingStateObserver: Observer<ScreenState> = Observer { state ->
        if (state != null)
            when (state) {
                ScreenState.LOADING -> {
                    tvNoInfoCurrent.visibility = View.VISIBLE
                    tvNoInfoCurrent.text = getString(R.string.fetching)
                    currentConnectionRetry.visibility = View.GONE
                    pbCurrentLocation.visibility = View.VISIBLE
                }
                ScreenState.SUCCESS -> {
                    tvNoInfoCurrent.visibility = View.GONE
                    currentConnectionRetry.visibility = View.VISIBLE
                    pbCurrentLocation.visibility = View.GONE
                }
                ScreenState.ERROR -> {
                    tvNoInfoCurrent.visibility = View.VISIBLE
                    tvNoInfoCurrent.text = getString(R.string.failed_get_location)
                    currentConnectionRetry.visibility = View.VISIBLE
                    pbCurrentLocation.visibility = View.GONE
                }
            }
    }
    private val currentLocationObserver: Observer<SunriseResponse?> = Observer { response ->
        if (response == null) {
            tvSunriseTime.visibility = View.GONE
            tvSunsetTime.visibility = View.GONE
        } else {
            tvSunriseTime.text = getTimeStringFromUtcString(response.results?.sunriseTime)
            tvSunsetTime.text = getTimeStringFromUtcString(response.results?.sunsetTime)
            tvSunriseTime.visibility = View.VISIBLE
            tvSunsetTime.visibility = View.VISIBLE
        }
    }
    private val cityLoadingStateObserver: Observer<ScreenState> = Observer { state ->
        if (state != null)
            when (state) {
                ScreenState.LOADING -> {
                    llCityTime.visibility = View.GONE
                    cityLoader.visibility = View.VISIBLE
                }
                ScreenState.SUCCESS -> {
                    llCityTime.visibility = View.VISIBLE
                    cityLoader.visibility = View.GONE
                }
                ScreenState.ERROR -> {
                    llCityTime.visibility = View.GONE
                    cityLoader.visibility = View.GONE
                }
            }
    }
    private val cityLocationObserver: Observer<SunriseResponse?> = Observer { response ->
        if (response == null) {
            llCityTime.visibility = View.GONE
        } else {
            tvCitySunriseTime.text = getTimeStringFromUtcString(response.results?.sunriseTime)
            tvCitySunsetTime.text = getTimeStringFromUtcString(response.results?.sunsetTime)
        }

    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SunriseViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.getCurrentLocationState().observeSafe(this, currentLoadingStateObserver)
        viewModel.getCurrentLocation().observeSafe(this, currentLocationObserver)
        viewModel.getCitySunriseState().observeSafe(this, cityLoadingStateObserver)
        viewModel.getCitySunrise().observeSafe(this, cityLocationObserver)
    }

    override fun initUi(view: View?) {
        initAutocompleteFragment()
        if (isPermissionGranted())
            viewModel.getCurrentSunriseTime()
        currentConnectionRetry.setOnClickListener {
            if (isPermissionGranted())
                viewModel.getCurrentSunriseTime()
            else
                requestLocationPermission()
        }
    }

    private fun initAutocompleteFragment() {
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.onPlaceSelected(place)
                tvCityName.text = place.name
            }

            override fun onError(@NonNull status: Status) {
                showErrorAlerter(getString(R.string.failed_get_location))
            }
        })
    }

    //region Permissions

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            val dialogBuilder = AlertDialog.Builder(activity)
                .setCancelable(true)
                .setMessage(R.string.location_needed)
                .setPositiveButton(
                    R.string.go_to_settings
                ) { dialog, _ ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", context?.packageName, null)
                    intent.data = uri
                    context?.startActivity(intent)
                    dialog?.dismiss()
                }
            val dialog = dialogBuilder.create()
            dialog.show()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION_REQUEST
            )

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_LOCATION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.getCurrentSunriseTime()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        }
    }

    //endregion

}
