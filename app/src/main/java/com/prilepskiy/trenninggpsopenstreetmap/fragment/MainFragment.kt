package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.MutableLiveData
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.location.LocationService
import com.prilepskiy.trenninggpsopenstreetmap.utils.DialogManager
import com.prilepskiy.trenninggpsopenstreetmap.utils.TimerUtils
import com.prilepskiy.trenninggpsopenstreetmap.utils.checkPermission
import com.prilepskiy.trenninggpsopenstreetmap.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask


class MainFragment : Fragment() {
    private var isServiceRunning = false
    private var timer: Timer? = null
    private var startTime=0L
    private val timeData=MutableLiveData<String>()
    lateinit var binding: FragmentMainBinding
    lateinit var pLauncher: ActivityResultLauncher<Array<String>>
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
    }

    private fun startStopService() {
        if (!isServiceRunning) {
            startLocService()
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fStartStop.setImageResource(R.drawable.ic_start)
        }
        isServiceRunning = !isServiceRunning
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

    private fun updateTime(){
timeData.observe(viewLifecycleOwner){
//binding.tvTime.text=TimerUtils.getTime(it.toLong())
    binding.tvTime.text=it
}
    }

    private fun startTimer(){
        timer?.cancel()
        timer= Timer()
        startTime=LocationService.startTime
        timer?.schedule(object :TimerTask(){
            override fun run() {
                activity?.runOnUiThread {
                    timeData.value=getCurrentTime()
                }
            }

        },1000,1000)
    }

    private fun getCurrentTime():String{
        return "Time: ${TimerUtils.getTime(System.currentTimeMillis()-startTime)}"
    }

    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
            timer?.cancel()
        }
        binding.fStartStop.setImageResource(R.drawable.baseline_stop_circle_24)
        LocationService.startTime=System.currentTimeMillis()
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
        binding.map.controller.setZoom(20.0)
        binding.map.controller.animateTo(GeoPoint(40.4167, -3.70325))
        val mLocationProvider = GpsMyLocationProvider(activity)
        val mLpcOverlay = MyLocationNewOverlay(mLocationProvider, binding.map)
        mLpcOverlay.enableMyLocation()
        mLpcOverlay.enableFollowLocation()
        mLpcOverlay.runOnFirstFix {
            binding.map.overlays.clear()
            binding.map.overlays.add(mLpcOverlay)
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

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}