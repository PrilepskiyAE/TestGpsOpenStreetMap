package com.prilepskiy.trenninggpsopenstreetmap.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.prilepskiy.trenninggpsopenstreetmap.MainActivity
import com.prilepskiy.trenninggpsopenstreetmap.R
import org.osmdroid.util.GeoPoint

class LocationService : Service() {
    private var lastlocation: Location? = null
    private var distance = 0.0f
    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var geopointList: ArrayList<GeoPoint>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        startNotification()
        startLocationUpdates()

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geopointList = ArrayList()
        isRunning = true
        initLocation()
    }

    override fun onDestroy() {
        isRunning = false
        locProvider.removeLocationUpdates(locCallback)
        super.onDestroy()

    }

    private var locCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            // p0.lastLocation
            val currentLocation = p0.lastLocation
            if (lastlocation != null && currentLocation != null) {
                if (currentLocation.speed  > 0.28f){

                distance += lastlocation?.distanceTo(currentLocation)!!
                geopointList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                val locModel = LocationModel(
                    currentLocation.speed, distance, geopointList
                )
                sendLocData(locModel)
            }
            }
            lastlocation = currentLocation
            Log.d("TAG", "onLocationResult:$distance ")


        }
    }

    private fun sendLocData(locationModel: LocationModel){
        val i =Intent(LOCMODEL_INTENT)
        i.putExtra(LOCMODEL_INTENT,locationModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
    }

    private fun startNotification() {

        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 10, nIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracker Running!")
            .setContentIntent(pIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
            Log.d("TAG", "startNotification: ")
        }

        startForeground(99, notification)

    }

    private fun initLocation() {
        locationRequest = LocationRequest.create()
        locationRequest.interval =
            PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("update_time_key","3000")?.toLong()!!
        locationRequest.fastestInterval =
            PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("update_time_key","3000")?.toLong()!!
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        locProvider.requestLocationUpdates(locationRequest, locCallback, Looper.myLooper())
    }

    companion object {
        const val LOCMODEL_INTENT="loc_intent"
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L

    }
}