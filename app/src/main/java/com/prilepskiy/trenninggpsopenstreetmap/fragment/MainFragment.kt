package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOSM()

    }
    private fun settingsOsm(){
        Configuration.getInstance().load(activity as AppCompatActivity,activity?.getSharedPreferences("osm_pref",
            Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue=BuildConfig.APPLICATION_ID
    }

    private fun initOSM(){
      binding.map.controller.setZoom(30.0)
        binding.map.controller.animateTo(GeoPoint(40.4167,-3.70325))
        val mLocationProvider=GpsMyLocationProvider(activity)
        val mLpcOverlay=MyLocationNewOverlay(mLocationProvider,binding.map)
        mLpcOverlay.enableMyLocation()
        mLpcOverlay.enableFollowLocation()
        mLpcOverlay.runOnFirstFix {
            binding.map.overlays.clear()
            binding.map.overlays.add(mLpcOverlay)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()}
}