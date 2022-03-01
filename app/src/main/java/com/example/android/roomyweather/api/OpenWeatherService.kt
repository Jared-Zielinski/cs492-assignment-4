package com.example.android.roomyweather.api

import com.example.android.roomyweather.data.FiveDayForecast
import com.example.android.roomyweather.data.OpenWeatherCityJsonAdapter
import com.example.android.roomyweather.data.OpenWeatherListJsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    /**
     * This method queries OpenWeather's Five Day Forecast API:
     *
     * https://openweathermap.org/forecast5
     *
     * @param city A city name. e.g. "Corvallis,OR,US".  This is plugged into the query string
     *   parameter `q` in the OpenWeather URL.
     * @param units One of "imperial", "metric", or "standard" to specify the type of units
     *   returned by the OpenWeather API.  This is plugged into the query string parameter `units`
     *   in the OpenWeather URL.
     * @param apiKey A valid OpenWeather API key.  This is plugged into the query string parameter
     *   `appid` in the OpenWeather URL.
     *
     * @return Returns a [FiveDayForecast] object representing the forecast retrieved from the
     *   OpenWeather API.
     */
    @GET("forecast")
    suspend fun loadFiveDayForecast(
        @Query("q") city: String?,
        @Query("units") units: String?,
        @Query("appid") apiKey: String
    ) : FiveDayForecast

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        /**
         * This method is used to create an instance of the [OpenWeatherService] interface.
         */
        fun create() : OpenWeatherService {
            val moshi = Moshi.Builder()
                .add(OpenWeatherListJsonAdapter())
                .add(OpenWeatherCityJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenWeatherService::class.java)
        }
    }
}