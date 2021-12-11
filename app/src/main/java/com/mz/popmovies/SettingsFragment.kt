package com.mz.popmovies

import androidx.preference.PreferenceFragmentCompat
import android.os.Bundle
import com.mz.popmovies.R

/**
 * Created by mateusz.zak on 27.03.2017.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_popmovies, rootKey)
    }
}