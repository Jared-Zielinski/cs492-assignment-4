package com.example.android.roomyweather.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.android.roomyweather.R
import com.example.android.roomyweather.data.ForecastCity
import com.example.android.roomyweather.data.ForecastPeriod
import com.example.android.roomyweather.util.getTempUnitsDisplay
import com.example.android.roomyweather.util.getWindSpeedUnitsDisplay
import com.example.android.roomyweather.util.openWeatherEpochToDate

const val EXTRA_FORECAST_PERIOD = "com.example.android.roomyweather.FORECAST_PERIOD"
const val EXTRA_FORECAST_CITY = "com.example.android.roomyweather.FORECAST_CITY"

class ForecastDetailActivity : AppCompatActivity() {
    private var forecastCity: ForecastCity? = null
    private var forecastPeriod: ForecastPeriod? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast_detail)

        if (intent != null && intent.hasExtra(EXTRA_FORECAST_CITY)) {
            forecastCity = intent.getSerializableExtra(EXTRA_FORECAST_CITY) as ForecastCity
            findViewById<TextView>(R.id.tv_forecast_city).text = forecastCity!!.name
        }

        if (intent != null && intent.hasExtra(EXTRA_FORECAST_PERIOD)) {
            forecastPeriod = intent.getSerializableExtra(EXTRA_FORECAST_PERIOD) as ForecastPeriod

            /*
             * Figure out the correct temperature and wind units to display for the current
             * setting of the units preference.
             */
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
            val units = sharedPrefs.getString(getString(R.string.pref_units_key), null)
            val tempUnitsDisplay = getTempUnitsDisplay(units, this)
            val windUnitsDisplay = getWindSpeedUnitsDisplay(units, this)

            Glide.with(this)
                .load(forecastPeriod!!.iconUrl)
                .into(findViewById(R.id.iv_forecast_icon))

            findViewById<TextView>(R.id.tv_forecast_date).text = getString(
                R.string.forecast_date_time,
                openWeatherEpochToDate(forecastPeriod!!.epoch, forecastCity!!.tzOffsetSec)
            )

            findViewById<TextView>(R.id.tv_low_temp).text = getString(
                R.string.forecast_temp,
                forecastPeriod!!.lowTemp,
                tempUnitsDisplay
            )

            findViewById<TextView>(R.id.tv_high_temp).text = getString(
                R.string.forecast_temp,
                forecastPeriod!!.highTemp,
                tempUnitsDisplay
            )

            findViewById<TextView>(R.id.tv_pop).text =
                getString(R.string.forecast_pop, forecastPeriod!!.pop)

            findViewById<TextView>(R.id.tv_clouds).text =
                getString(R.string.forecast_clouds, forecastPeriod!!.cloudCover)

            findViewById<TextView>(R.id.tv_wind).text = getString(
                R.string.forecast_wind,
                forecastPeriod!!.windSpeed,
                windUnitsDisplay
            )

            findViewById<ImageView>(R.id.iv_wind_dir).rotation =
                forecastPeriod!!.windDirDeg.toFloat()

            findViewById<TextView>(R.id.tv_forecast_description).text =
                forecastPeriod!!.description
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_forecast_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareForecastText()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method constructs a simple string of text representing the current forecast period
     * and opens the Android Sharesheet to share that string.
     */
    private fun shareForecastText() {
        if (forecastCity != null && forecastPeriod != null) {
            /*
             * Figure out the correct temperature and wind units to display for the current
             * setting of the units preference.
             */
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
            val units = sharedPrefs.getString(getString(R.string.pref_units_key), null)
            val tempUnitsDisplay = getTempUnitsDisplay(units, this)

            val date = openWeatherEpochToDate(forecastPeriod!!.epoch, forecastCity!!.tzOffsetSec)
            val shareText = getString(
                R.string.share_forecast_text,
                getString(R.string.app_name),
                forecastCity!!.name,
                getString(R.string.forecast_date_time, date),
                forecastPeriod!!.description,
                getString(R.string.forecast_temp, forecastPeriod!!.highTemp, tempUnitsDisplay),
                getString(R.string.forecast_temp, forecastPeriod!!.lowTemp, tempUnitsDisplay),
                getString(R.string.forecast_pop, forecastPeriod!!.pop)
            )

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, null))
        }
    }
}