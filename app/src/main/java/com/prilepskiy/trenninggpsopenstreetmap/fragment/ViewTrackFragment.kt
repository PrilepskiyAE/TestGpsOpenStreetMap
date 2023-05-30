package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ActivityMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentMainBinding
import com.prilepskiy.trenninggpsopenstreetmap.databinding.ViewTracksBinding


class ViewTrackFragment : Fragment() {
    lateinit var binding: ViewTracksBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ViewTracksBinding.inflate(inflater)
            return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = ViewTrackFragment()}
}