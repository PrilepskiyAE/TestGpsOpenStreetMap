package com.prilepskiy.trenninggpsopenstreetmap.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.prilepskiy.trenninggpsopenstreetmap.MainActivity
import com.prilepskiy.trenninggpsopenstreetmap.R

class LocationService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        startNotification()


        return START_STICKY
    }

    override fun onCreate() {
        Log.d("TAG", "onCreate: Location Service start ")
        super.onCreate()
        isRunning=true
    }

    override fun onDestroy() {
        Log.d("TAG", "onCreate: Location Service stop ")
        isRunning=false
        super.onDestroy()

    }

    private fun startNotification() {

        val nIntent=Intent(this,MainActivity::class.java)
        val pIntent=PendingIntent.getActivity(this, 10, nIntent,  PendingIntent.FLAG_IMMUTABLE)
        val notification=NotificationCompat
            .Builder(this,CHANNEL_ID)
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
          val  nManager=getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
            Log.d("TAG", "startNotification: ")
        }

        startForeground(99,notification)

    }

    companion object {
        const val CHANNEL_ID = "channel_1"
        var isRunning =false
        var startTime=0L

    }
}