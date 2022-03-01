package com.example.android.roomyweather.data

import com.example.android.roomyweather.api.OpenWeatherService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class FiveDayForecastRepository(
    private val service: OpenWeatherService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /*
     * These three properties are used to implement a basic caching strategy, where an API call
     * is only executed if the requested city or units don't match the ones from the previous
     * API call.
     */
    private var currentCity: String? = null
    private var currentUnits: String? = null
    private var cachedForecast: FiveDayForecast? = null

    /**
     * This method triggers a call to the OpenWeather API to fetch new five-day forecast data.
     * The API response is cached, and the cached response is returned if the requested `city`
     * and `units` match the parameters of the previous API call.  Otherwise, a new API call is
     * made.
     *
     * @param city A city name. e.g. "Corvallis,OR,US".
     * @param units One of "imperial", "metric", or "standard" to specify the type of units
     *   returned by the OpenWeather API.
     * @param apiKey A valid OpenWeather API key.
     *
     * @return Returns a Kotlin [Result] object wrapping the API response data.
     */
    suspend fun loadFiveDayForecast(
        city: String?,
        units: String?,
        apiKey: String
    ) : Result<FiveDayForecast> {
        /*
         * If we have a cached forecast for the same city and units, return the cached forecast
         * without making a network call.  Otherwise, make an API call (in a background thread) to
         * fetch new forecast data and cache it before returning it.
         */
        return if (city == currentCity && units == currentUnits && cachedForecast!= null) {
            Result.success(cachedForecast!!)
        } else {
            currentCity = city
            currentUnits = units
            withContext(ioDispatcher) {
                try {
                    val forecast = service.loadFiveDayForecast(city, units, apiKey)
                    cachedForecast = forecast
                    Result.success(forecast)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
}