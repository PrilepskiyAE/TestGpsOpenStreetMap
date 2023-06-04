package com.prilepskiy.trenninggpsopenstreetmap.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.TrackItemBinding

class TrackAdapter : ListAdapter<TrackItem, TrackAdapter.Holder>(Comporator()) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = TrackItemBinding.bind(view)
        fun bind(trackItem: TrackItem) = with(binding) {
            tvDate.text = trackItem.date
            tvSpeed.text=trackItem.velocity
            tvTime.text=trackItem.time
            tvDistance.text=trackItem.distance

        }
    }

    class Comporator:DiffUtil.ItemCallback<TrackItem>(){
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}