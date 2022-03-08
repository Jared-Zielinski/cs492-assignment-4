package com.example.android.roomyweather.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.android.roomyweather.api.OpenWeatherService
import com.example.android.roomyweather.data.*
import kotlinx.coroutines.launch

class FiveDayForecastViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FiveDayForecastRepository(OpenWeatherService.create())
    private val citiesRepo = CitiesRepository(
        AppDatabase.getInstance(application).searchedCityDao()
    )

    /*
     * These fields hold the five-day forecast data displayed by the UI.
     */
    private val _forecast = MutableLiveData<FiveDayForecast?>(null)
    val forecast: LiveData<FiveDayForecast?> = _forecast

    /*
     * These fields hold an error object to be used to display an error message in the UI if
     * there was an error making an API call to OpenWeather.
     */
    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    /*
     * These fields hold a boolean value indicating whether an API call is currently loading.
     */
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * This method triggers a call to the OpenWeather API's Five Day Forecast API.  The [LiveData]
     * properties above are updated accordingly based on the state of the API call.
     *
     * @param city A city name. e.g. "Corvallis,OR,US".
     * @param units One of "imperial", "metric", or "standard" to specify the type of units
     *   returned by the OpenWeather API.
     * @param apiKey A valid OpenWeather API key.
     */
    fun loadFiveDayForecast(city: String?, units: String?, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            if(!city.isNullOrBlank()){
                val cityEntry = SearchedCityEntry(city,System.currentTimeMillis())
                citiesRepo.insertCity(cityEntry)
            }
            val result = repository.loadFiveDayForecast(city, units, apiKey)
            _loading.value = false
            _error.value = result.exceptionOrNull()
            _forecast.value = result.getOrNull()
        }
    }
}