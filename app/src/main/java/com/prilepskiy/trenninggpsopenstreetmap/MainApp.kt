package com.prilepskiy.trenninggpsopenstreetmap

import android.app.Application
import com.prilepskiy.trenninggpsopenstreetmap.db.MainDb

class MainApp :Application() {
    val database by lazy{
        MainDb.getDataBase(this)
    }


}