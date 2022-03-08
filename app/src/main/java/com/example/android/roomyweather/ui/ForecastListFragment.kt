package com.example.android.roomyweather.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomyweather.R
import com.example.android.roomyweather.data.ForecastPeriod
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar


class ForecastListFragment : Fragment(R.layout.forecast_list), SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: FiveDayForecastViewModel by viewModels()
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var forecastListRV: RecyclerView
    private lateinit var loadingErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingErrorTV = view.findViewById(R.id.tv_loading_error)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        forecastListRV = view.findViewById(R.id.rv_forecast_list)

        forecastAdapter = ForecastAdapter(::onForecastItemClick)

        forecastListRV.layoutManager = LinearLayoutManager(requireContext())
        forecastListRV.setHasFixedSize(true)
        forecastListRV.adapter = forecastAdapter
        setHasOptionsMenu(true)

        /*
         * Observe forecast data.  Whenever forecast data changes, display it in the RecyclerView.
         */
        viewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            if (forecast != null) {
                forecastAdapter.updateForecast(forecast)
                forecastListRV.visibility = View.VISIBLE
                forecastListRV.scrollToPosition(0)

                (activity as AppCompatActivity).title = forecast.city.name
            }
        }

        /*
         * Observe error message.  If an error message is present, display it.
         */
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                loadingErrorTV.text = getString(R.string.loading_error, error.message)
                loadingErrorTV.visibility = View.VISIBLE
            }
        }

        /*
         * Observe loading indicator.  When loading, display progress indicator and hide other
         * UI elements.
         */
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingIndicator.visibility = View.VISIBLE
                loadingErrorTV.visibility = View.INVISIBLE
                forecastListRV.visibility = View.INVISIBLE
            } else {
                loadingIndicator.visibility = View.INVISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /*
         * Here, we're reading the current preference values and triggering a data fetching
         * operation in onResume().  This avoids the need to set up a preference change listener.
         * It also means that a new API call could potentially be made every time the activity
         * is resumed.  However, because of the basic caching that's implemented in the
         * FiveDayForecastRepository class, an API call will actually only be made whenever
         * the city or units setting changes (which is exactly what we want).
         */
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
        val city = sharedPrefs.getString(getString(R.string.pref_city_key), "Corvallis,OR,US")
        val units = sharedPrefs.getString(getString(R.string.pref_units_key), null)

        viewModel.loadFiveDayForecast(city, units, OPENWEATHER_APPID)
    }

    override fun onPause() {
        super.onPause()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun onForecastItemClick(forecastPeriod: ForecastPeriod) {
        val directions = ForecastListFragmentDirections.navigateToForecastDetail(forecastAdapter.forecastCity!!, forecastPeriod)
        findNavController().navigate(directions)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == getString(R.string.pref_city_key)){
            onResume()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.forecast_list_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map -> {
                viewForecastCityOnMap()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method generates a geo URI to represent location of the city for which the forecast
     * is being displayed and uses an implicit intent to view that location on a map.
     */
    private fun viewForecastCityOnMap() {
        if (forecastAdapter.forecastCity != null) {
            val geoUri = Uri.parse(getString(
                R.string.geo_uri,
                forecastAdapter.forecastCity?.lat ?: 0.0,
                forecastAdapter.forecastCity?.lon ?: 0.0,
                11
            ))
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                /*
                 * If there is no available app for viewing geo locations, display an error
                 * message in a Snackbar.
                 */
                Snackbar.make(
                    //findViewById(R.id.coordinator_layout),
                    (activity as AppCompatActivity).findViewById(R.id.drawer_layout),
                    R.string.action_map_error,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}