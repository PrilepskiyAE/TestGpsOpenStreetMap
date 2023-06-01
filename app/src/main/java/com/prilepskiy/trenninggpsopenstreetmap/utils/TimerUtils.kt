package com.prilepskiy.trenninggpsopenstreetmap.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

import java.util.*

object TimerUtils {


    @SuppressLint("SimpleDateFormat")
    private var timeFormatter=SimpleDateFormat("HH:mm:ss")
    fun getTime(timeInMillis:Long):String{
        val cv=Calendar.getInstance()
        timeFormatter.timeZone= TimeZone.getTimeZone("UTC")
        cv.timeInMillis=timeInMillis
        return timeFormatter.format(cv.time)
    }
}