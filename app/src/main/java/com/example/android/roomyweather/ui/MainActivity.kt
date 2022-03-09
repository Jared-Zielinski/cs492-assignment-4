package com.example.android.roomyweather.ui

import android.content.ClipData
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.roomyweather.BuildConfig
import com.example.android.roomyweather.R
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomyweather.data.AppDatabase
import com.example.android.roomyweather.data.CitiesRepository
import com.example.android.roomyweather.data.SearchedCityEntry

/*
 * To use your own OpenWeather API key, create a file called `gradle.properties` in your
 * GRADLE_USER_HOME directory (this will usually be `$HOME/.gradle/` in MacOS/Linux and
 * `$USER_HOME/.gradle/` in Windows), and add the following line:
 *
 *   OPENWEATHER_API_KEY="<put_your_own_OpenWeather_API_key_here>"
 *
 * The Gradle build for this project is configured to automatically grab that value and store
 * it in the field `BuildConfig.OPENWEATHER_API_KEY` that's used below.  You can read more
 * about this setup on the following pages:
 *
 *   https://developer.android.com/studio/build/gradle-tips#share-custom-fields-and-resource-values-with-your-app-code
 *
 *   https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
 *
 * Alternatively, you can just hard-code your API key below 🤷‍.
 */
const val OPENWEATHER_APPID = BuildConfig.OPENWEATHER_API_KEY

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val citiesViewModel: BookmarkedCitiesViewModel by viewModels()
    private lateinit var citiesListRV: RecyclerView
    private val cityListAdapter = BookmarkedCityListAdapter(::onCityClick)


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        citiesListRV = findViewById(R.id.rv_city_list)
        citiesListRV.layoutManager = LinearLayoutManager(this)
        citiesListRV.setHasFixedSize(true)
        citiesListRV.adapter = cityListAdapter
        citiesListRV.visibility = View.VISIBLE
        citiesListRV.scrollToPosition(0)


        //Create the toolbar found at the top of the screen
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //navHostFragment is the fragment that will contain our other fragments
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //navController controls which fragment will populate the navHostFragment
        val navController = navHostFragment.navController

        //Layout for the nav drawer
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //Use the nav graph and drawer layout to manage the toolbar and create the nav drawer
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

//        findViewById<NavigationView>(R.id.nav_view).setOnClickListener {
//            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()
//        }



        citiesViewModel.loadBookmarkedCities()
        citiesViewModel.cities.observe(this) { cities ->
            cityListAdapter.updateCitylist(cities)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun onCityClick(clickedCity: SearchedCityEntry){
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        with (sharedPrefs.edit()) {
            putString(getString(R.string.pref_city_key), clickedCity.location)
            apply()
        }

        findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()

        val fiveDayForecastViewModel: FiveDayForecastViewModel by viewModels()
        fiveDayForecastViewModel.loadFiveDayForecast(
            clickedCity.location,
            sharedPrefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default_value)),
            OPENWEATHER_APPID)
    }
}