package com.example.android.roomyweather.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchedCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: SearchedCityEntry)

    @Delete
    suspend fun  delete(city: SearchedCityEntry)

    @Query("SELECT * FROM SearchedCityEntry ORDER BY lastAccessed DESC")
    fun getAllCities(): Flow<List<SearchedCityEntry>>

    @Query("SELECT * FROM SearchedCityEntry ORDER BY lastAccessed DESC")
    suspend fun getAllCitiesNoFlow(): List<SearchedCityEntry>
}