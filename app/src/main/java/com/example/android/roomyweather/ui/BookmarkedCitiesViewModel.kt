package com.example.android.roomyweather.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.roomyweather.data.AppDatabase
import com.example.android.roomyweather.data.CitiesRepository
import com.example.android.roomyweather.data.SearchedCityEntry
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkedCitiesViewModel(application: Application): AndroidViewModel(application) {
    private val _cities = MutableLiveData<List<SearchedCityEntry>>(null)
    var cities: LiveData<List<SearchedCityEntry>> = _cities

    private val citiesRepo = CitiesRepository(
        AppDatabase.getInstance(application).searchedCityDao()
    )

    fun loadBookmarkedCities() {
        cities = citiesRepo.getCities().asLiveData()
    }
}