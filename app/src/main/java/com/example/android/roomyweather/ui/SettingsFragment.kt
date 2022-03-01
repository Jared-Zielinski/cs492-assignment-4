package com.example.android.roomyweather.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.android.roomyweather.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}