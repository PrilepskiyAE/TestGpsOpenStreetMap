package com.prilepskiy.trenninggpsopenstreetmap.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prilepskiy.trenninggpsopenstreetmap.location.LocationModel

class MainViewModel:ViewModel() {
    val locationUpdate=MutableLiveData<LocationModel>()
    val timeData=MutableLiveData<String>()
}