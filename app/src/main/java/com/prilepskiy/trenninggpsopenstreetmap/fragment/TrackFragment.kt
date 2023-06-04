package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.prilepskiy.trenninggpsopenstreetmap.MainApp
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentViewTrackBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ViewTracksBinding
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackAdapter
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackItem
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline


class TrackFragment : Fragment() {
    private val model: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }
    lateinit var binding: ViewTracksBinding



    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        // Inflate the layout for this fragment
        binding = ViewTracksBinding.inflate(inflater)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrack()
    }

    private fun getTrack(){
        model.currentTrack.observe(viewLifecycleOwner){
            with(binding){
                tvAverageVel.text="Averag velocity ${it.velocity} km/h"
                tvData.text="Date: ${it.date}"
                tvDistance.text="Distance: ${it.distance}"
                tvTime.text="Time: ${it.time}"
                val polyline=getPolyline(it.geoPoint)
                map.overlays.add(polyline)
                goToStartPosition(polyline.actualPoints[0])
            }
        }
    }

    private fun goToStartPosition(start:GeoPoint){
        binding.map.controller.zoomTo(18.0)
        binding.map.controller.animateTo(start)
    }
    private fun getPolyline(geoPoint: String):Polyline{
        val polyline=Polyline()
        val list=geoPoint.split("/")
        list.forEach {
            if (it.isEmpty())return@forEach
            val points=it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(),points[1].toDouble()))
        }

        return polyline
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity, activity?.getSharedPreferences(
                "osm_pref",
                Context.MODE_PRIVATE
            )
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
    companion object {

        @JvmStatic
        fun newInstance() = TrackFragment()}
}