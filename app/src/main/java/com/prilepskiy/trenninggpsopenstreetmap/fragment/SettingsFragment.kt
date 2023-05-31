package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.utils.showToast

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var timePref: Preference
    private lateinit var colorPref: Preference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)
        init()

    }

    private fun init() {
        timePref = findPreference("update_time_key")!!
        colorPref = findPreference("color_key")!!
        val changeListener = onChangeListener()
        timePref.onPreferenceChangeListener = changeListener
        colorPref.onPreferenceChangeListener = changeListener
        initPref()
    }

    private fun onChangeListener(): Preference.OnPreferenceChangeListener {
        return Preference.OnPreferenceChangeListener { pref, value ->
            when (pref.key) {
                "update_time_key" -> {
                    onTimeChange(value.toString())
                }

                "color_key" -> {
                    pref.icon?.setTint(Color.parseColor(value.toString()))
                }
            }

            true
        }

    }

    private fun onTimeChange(value: String) {
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref.title.toString().substringBefore(":")
        timePref.title = "$title : ${nameArray[valueArray.indexOf(value)]}"
    }

    private fun initPref() {
        val pref = timePref.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref.title
        timePref.title =
            "$title : ${nameArray[valueArray.indexOf(pref?.getString("update_time_key", "3000"))]}"

        colorPref.icon?.setTint(Color.parseColor(pref?.getString("color_key", "#FF0091EA")))
    }


}