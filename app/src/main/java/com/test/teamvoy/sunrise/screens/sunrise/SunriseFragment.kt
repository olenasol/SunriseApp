package com.test.teamvoy.sunrise.screens.sunrise

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.test.teamvoy.sunrise.R
import com.test.teamvoy.sunrise.base.BaseFragment
import com.test.teamvoy.sunrise.networking.base.ScreenState
import com.test.teamvoy.sunrise.util.observeSafe
import kotlinx.android.synthetic.main.fragment_sunrise.*


class SunriseFragment : BaseFragment<SunriseViewModel>() {

    //region Observers

    private val screenStateObserver: Observer<ScreenState> = Observer { state ->
        if (state != null)
            when (state) {
                ScreenState.LOADING -> {
                    screenProgressBar.visibility = View.VISIBLE
                }
                ScreenState.SUCCESS -> {
                    screenProgressBar.visibility = View.GONE
                }
                ScreenState.ERROR -> {
                    screenProgressBar.visibility = View.GONE
                }
            }
    }

    //endregion

    companion object {
        fun newInstance(): SunriseFragment {
            return SunriseFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SunriseViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    fun subscribeToObservers() {
        viewModel.getScreenState().observeSafe(this, screenStateObserver)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sunrise
    }

    override fun initUi(view: View?) {
        viewModel.getCurrentSunriseTime()
    }


}
