package com.example.android.roomyweather.data

import com.squareup.moshi.Json

/**
 * This class represents a response from the OpenWeather API's Five Day Forecast API:
 *
 * https://openweathermap.org/forecast5
 */
data class FiveDayForecast(
    @Json(name = "list") val periods: List<ForecastPeriod>,
    val city: ForecastCity
)
