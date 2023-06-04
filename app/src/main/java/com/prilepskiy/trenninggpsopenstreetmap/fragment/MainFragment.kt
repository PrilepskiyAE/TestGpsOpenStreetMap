package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.media.audiofx.Equalizer.Settings
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackItem
import com.prilepskiy.trenninggpsopenstreetmap.location.LocationModel
import com.prilepskiy.trenninggpsopenstreetmap.location.LocationService
import com.prilepskiy.trenninggpsopenstreetmap.utils.DialogManager
import com.prilepskiy.trenninggpsopenstreetmap.utils.TimerUtils
import com.prilepskiy.trenninggpsopenstreetmap.utils.checkPermission
import com.prilepskiy.trenninggpsopenstreetmap.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask


class MainFragment : Fragment() {
    private var locationModel: LocationModel?=null
    private var isServiceRunning = false
    private var firstStart=true
    private var timer: Timer? = null
    private var startTime = 0L
    private var pl:Polyline?=null
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private val model: MainViewModel by activityViewModels()
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
        registerPermission()
        // startLocService()
        setOnClick()
        checkServiceState()
        updateTime()
        registerLocResiver()
        locationUpdate()
    }

    private fun startStopService() {
        if (!isServiceRunning) {
            startLocService()
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fStartStop.setImageResource(R.drawable.ic_start)
            timer?.cancel()
            DialogManager.showSaveDialog(requireContext(),getTrackItem(),object :DialogManager.Listener {
                override fun onClick() {
                    showToast("Saved")
                }

            } )
        }
        isServiceRunning = !isServiceRunning
    }

private fun getTrackItem():TrackItem{
   return TrackItem(null,getCurrentTime(),TimerUtils.getDate(),String.format("%.1f", locationModel?.distance?.div(1000)),
        getAverageSpeed(locationModel?.distance?:0.0f),geoPointToString(locationModel?.geoPointList?: arrayListOf())

    )
}
    private fun checkServiceState() {
        isServiceRunning = LocationService.isRunning
        if (isServiceRunning) {
            binding.fStartStop.setImageResource(R.drawable.baseline_stop_circle_24)
            startTimer()
        } else {
            binding.fStartStop.setImageResource(R.drawable.ic_start)
        }
    }

    private fun setOnClick() {
        val listener = onClick()
        binding.fStartStop.setOnClickListener(listener)
    }

    private fun onClick(): View.OnClickListener {
        return View.OnClickListener {
            when (it.id) {
                R.id.fStartStop -> {
                    startStopService()
                }
            }
        }
    }

    private fun updateTime() {
        model.timeData.observe(viewLifecycleOwner) {
//binding.tvTime.text=TimerUtils.getTime(it.toLong())
            binding.tvTime.text = it
        }
    }
    private fun locationUpdate(){
        model.locationUpdate.observe(viewLifecycleOwner){
            val distance="Distance: ${String.format("%.1f",it.distance)} m"
            val velocity="Velocity ${String.format("%.1f",3.6 * it.velocity)} km/h"
            val aVelocity="Average velocity ${getAverageSpeed(it.distance)} km/h"
            binding.tvDistance.text=distance
            binding.tvVelocity.text=velocity
            binding.tvAverageVel.text=aVelocity
           locationModel=it
            updatePolyline(it.geoPointList)

        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    model.timeData.value = getCurrentTime()
                }
            }

        }, 1000, 1000)
    }

    private fun getCurrentTime(): String {
        return "Time: ${TimerUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))

        } else {
            activity?.startService(Intent(activity, LocationService::class.java))

        }
        binding.fStartStop.setImageResource(R.drawable.baseline_stop_circle_24)
        LocationService.startTime = System.currentTimeMillis()
        startTimer()

    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()
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

    private fun initOSM() {
        checkLocationEnabled()
        pl= Polyline()
        pl?.outlinePaint?.color = Color.BLUE
        binding.map.controller.setZoom(20.0)
        binding.map.controller.animateTo(GeoPoint(40.4167, -3.70325))
        val mLocationProvider = GpsMyLocationProvider(activity)
        val mLpcOverlay = MyLocationNewOverlay(mLocationProvider, binding.map)
        mLpcOverlay.enableMyLocation()
        mLpcOverlay.enableFollowLocation()
        mLpcOverlay.runOnFirstFix {
            binding.map.overlays.clear()
            binding.map.overlays.add(mLpcOverlay)
            binding.map.overlays.add(pl)
        }
    }

    private fun registerPermission() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    initOSM()
                } else {
                    showToast("permission error")

                }
            }
    }

    private fun checkLocPermission() {
        Log.d("TAG", "checkLocPermission:0 ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("TAG", "checkLocPermission: 1")
            checkPermission10()
        } else {
            checkPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission10() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            Log.d("TAG", "checkLocPermission: 3")
            initOSM()
        } else {
            Log.d("TAG", "checkLocPermission: 4")
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkPermission() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOSM()
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun checkLocationEnabled() {
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            showToast("GPS Error")
            DialogManager.showLocEnabledDialog(activity as AppCompatActivity,
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == LocationService.LOCMODEL_INTENT) {
                val locationModel = p1.getSerializableExtra(
                    LocationService.LOCMODEL_INTENT,
                    LocationModel::class.java
                )
                Log.d("TAG", "onReceive: $locationModel")
                model.locationUpdate.value=locationModel
            }
        }
    }

    private fun registerLocResiver() {
        val locFilter = IntentFilter(LocationService.LOCMODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locFilter)

    }

    private fun getAverageSpeed(dis:Float):String{
        return String.format("%.1f",(dis/((System.currentTimeMillis()-startTime)/1000.0f)))
    }

    private fun addPoint(list: List<GeoPoint>){
        pl?.addPoint(list[list.size-1])
    }

   private fun fillPolyline(list: List<GeoPoint>){
       list.forEach {
           pl?.addPoint(it)
       }
   }

    private fun updatePolyline(list: List<GeoPoint>){
        if (list.size>1 && firstStart){
            fillPolyline(list)
            firstStart=false
        } else{
            addPoint(list)
        }
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiver)
    }


    private fun geoPointToString(list: List<GeoPoint>):String{
       val sb=StringBuilder()
        list.forEach {
            sb.append("${it.latitude},${it.longitude}/")
        }
        return sb.toString()
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}