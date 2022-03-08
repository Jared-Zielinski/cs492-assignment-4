package com.example.android.roomyweather.data

class CitiesRepository(private val dao: SearchedCityDao) {
    suspend fun insertCity(city: SearchedCityEntry) = dao.insert(city)
    suspend fun removeCity(city: SearchedCityEntry) = dao.insert(city)
    fun getCities() = dao.getAllCities()
    suspend fun getAllCitiesNoFlow() = dao.getAllCitiesNoFlow()
}