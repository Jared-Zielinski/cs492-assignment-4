package com.example.android.roomyweather.data

import com.squareup.moshi.FromJson
import java.io.Serializable

/**
 * This class represents the `city` portion of the response from a call to OpenWeather's
 * Five Day Forecast API: https://openweathermap.org/forecast5.
 */
data class ForecastCity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val tzOffsetSec: Int
) : Serializable

/* ******************************************************************************************
 * Below is a set of classes used to parse the JSON response from the OpenWeather API into
 * a ForecastCity object.  The first two classes are designed to match the structure of the
 * the `city` field in the OpenWeather 5-day forecast API's JSON response.  The last is a
 * custom type adapter that can be used with Moshi to parse OpenWeather JSON directly into
 * a ForecastCity object.
 * ******************************************************************************************/

data class OpenWeatherCityJson(
    val name: String,
    val coord: OpenWeatherCityCoordJson,
    val timezone: Int
)

data class OpenWeatherCityCoordJson(
    val lat: Double,
    val lon: Double
)

/**
 * This class is a custom Moshi type adapter (https://github.com/square/moshi/#custom-type-adapters)
 * for parsing the `city` portion of the response from the OpenWeather Five Day Forecast API:
 *
 * https://openweathermap.org/forecast5
 *
 * It uses the classes above to directly model the structure of the OpenWeather API response and
 * then extracts the relevant fields from those classes into a flat-structured [ForecastCity]
 * object.
 */
class OpenWeatherCityJsonAdapter {
    @FromJson
    fun forecastCityFromJson(city: OpenWeatherCityJson) = ForecastCity(
        name = city.name,
        lat = city.coord.lat,
        lon = city.coord.lon,
        tzOffsetSec = city.timezone
    )
}