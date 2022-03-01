package com.example.android.roomyweather.util

import android.content.Context
import com.example.android.roomyweather.R
import java.util.*

const val FIVE_DAY_FORECAST_DEFAULT_TZ = "UTC"
const val TZ_OFFSET_FORMAT_STR = "GMT%0+3d:%02d"

/**
 * This function computes an actual date/time in the correct timezone from the epoch and timezone
 * offset (in seconds) returned by the OpenWeather API.  The date/time is returned as a Calendar.
 *
 * @param epoch An epoch, e.g. from the `dt` field of the OpenWeather API response.
 * @param tzOffsetSec The number of seconds offset from GMT characterizing the local timezone,
 *   e.g. as from the `timezone` field of the OpenWeather API response.
 */
fun openWeatherEpochToDate(epoch: Int, tzOffsetSec: Int): Calendar {
    val date = Calendar.getInstance(TimeZone.getTimeZone(FIVE_DAY_FORECAST_DEFAULT_TZ))
    date.timeInMillis = epoch.toLong() * 1000L
    val tzOffsetHours = tzOffsetSec / 3600
    val tzOffsetMin = (Math.abs(tzOffsetSec) % 3600) / 60
    val localTZId = TZ_OFFSET_FORMAT_STR.format(tzOffsetHours, tzOffsetMin)
    date.timeZone = TimeZone.getTimeZone(localTZId)
    return date
}

/**
 * Returns the correct temperature unit string to display for a given setting of the units
 * preference, e.g. "F" for imperial units.
 *
 * @param units The current value of the pref_units preference ("standard", "metric", or
 *              "imperial").
 * @param context A context.
 * @return Returns the correct temperature unit display string for the given value of `units`,
 *   e.g. "F" for imperial.
 */
fun getTempUnitsDisplay(units: String?, context: Context) : String {
    return when (units) {
        context.getString(R.string.pref_units_value_standard) -> context.getString(R.string.standard_temp_display)
        context.getString(R.string.pref_units_value_metric) -> context.getString(R.string.metric_temp_display)
        else -> context.getString(R.string.imperial_temp_display)
    }
}

/**
 * Returns the correct wind speed unit string to display for a given setting of the units
 * preference, e.g. "mph" for imperial units.
 *
 * @param units The current value of the pref_units preference ("standard", "metric", or
 *              "imperial").
 * @param context A context.
 * @return Returns the correct wind speed unit display string for the given value of `units`, e.g.
 *   "mph" for imperial.
 */
fun getWindSpeedUnitsDisplay(units: String?, context: Context) : String {
    return when (units) {
        context.getString(R.string.pref_units_value_standard) -> context.getString(R.string.standard_wind_display)
        context.getString(R.string.pref_units_value_metric) -> context.getString(R.string.metric_wind_display)
        else -> context.getString(R.string.imperial_wind_display)
    }
}