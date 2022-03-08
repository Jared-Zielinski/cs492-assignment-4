package com.example.android.roomyweather.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchedCityEntry(
    @PrimaryKey
    val location: String,
    val lastAccessed: Long
)
