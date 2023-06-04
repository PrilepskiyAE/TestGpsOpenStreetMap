package com.prilepskiy.trenninggpsopenstreetmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.prilepskiy.trenninggpsopenstreetmap.MainApp
import com.prilepskiy.trenninggpsopenstreetmap.databinding.FragmentTrackListBinding
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackAdapter
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackItem
import com.prilepskiy.trenninggpsopenstreetmap.utils.openFragment

class TrakcListFragment : Fragment(), TrackAdapter.Listener {
    private lateinit var binding: FragmentTrackListBinding
    lateinit var adapter: TrackAdapter
    private val model: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getListFromDb()
    }

    private fun initAdapter() = with(binding) {
        adapter = TrackAdapter(this@TrakcListFragment)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter


    }

    private fun getListFromDb() {
        model.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.textView4.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TrakcListFragment()
    }

    override fun onClick(trackItem: TrackItem, type: TrackAdapter.ClickType) {
        when(type){
            TrackAdapter.ClickType.OPEN->{
                openFragment(TrackFragment.newInstance())
                model.currentTrack.value=trackItem
            }
            TrackAdapter.ClickType.DELETE->{
                model.deleteTrack(trackItem)
            }
        }

    }
}