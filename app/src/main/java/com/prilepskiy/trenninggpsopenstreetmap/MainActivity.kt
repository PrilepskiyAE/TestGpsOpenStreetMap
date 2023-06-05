package com.prilepskiy.trenninggpsopenstreetmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.fragment.MainFragment
import com.prilepskiy.trenninggpsopenstreetmap.fragment.SettingsFragment
import com.prilepskiy.trenninggpsopenstreetmap.fragment.TrakcListFragment
import com.prilepskiy.trenninggpsopenstreetmap.utils.openFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBottomNavClicks()
        openFragment(MainFragment.newInstance())
    }
    private fun onBottomNavClicks(){
binding.bNav.setOnItemSelectedListener {
    when(it.itemId){
        R.id.id_home->{openFragment(MainFragment.newInstance())}
        R.id.id_tracks->{openFragment(TrakcListFragment.newInstance())}
        R.id.id_settings->{openFragment(SettingsFragment())}
    }
    true
}
    }
}