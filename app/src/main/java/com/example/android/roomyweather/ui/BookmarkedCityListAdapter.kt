package com.example.android.roomyweather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomyweather.R
import com.example.android.roomyweather.data.SearchedCityEntry

class BookmarkedCityListAdapter(private val onClick: (SearchedCityEntry) -> Unit)
    : RecyclerView.Adapter<BookmarkedCityListAdapter.ViewHolder>() {

    var citiesList = listOf<SearchedCityEntry>()

    fun updateCitylist(cities: List<SearchedCityEntry>){
        citiesList = cities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cities_list_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.citiesList[position])
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    class ViewHolder(val cityView: View, val onClick: (SearchedCityEntry) -> Unit)
        : RecyclerView.ViewHolder(cityView){
        private val cityTV: TextView = cityView.findViewById(R.id.tv_citylist_city)
        private var currentCity: SearchedCityEntry? = null

        init {
            cityView.setOnClickListener{
                currentCity?.let(onClick)
            }
        }

        fun bind(cityEntry: SearchedCityEntry){
            currentCity = cityEntry
            cityTV.text = cityEntry.location
        }
    }
}