package com.prilepskiy.trenninggpsopenstreetmap.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.prilepskiy.trenninggpsopenstreetmap.db.MainDb
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackItem
import com.prilepskiy.trenninggpsopenstreetmap.location.LocationModel
import kotlinx.coroutines.launch

class MainViewModel(db:MainDb):ViewModel() {
    val locationUpdate=MutableLiveData<LocationModel>()
    val timeData=MutableLiveData<String>()
    val dao=db.getDao()
    val tracks=dao.getAllTrack().asLiveData()
    val currentTrack=MutableLiveData<TrackItem>()
    fun insertTrack(trackItem: TrackItem){
        viewModelScope.launch {
            dao.insertTrack(trackItem)
        }
    }

    fun deleteTrack(trackItem: TrackItem){
        viewModelScope.launch {
            dao.deleteTrack(trackItem)
        }
    }

    class ViewModelFactory(private val db:MainDb):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(db)as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
        }

    }
}