package com.prilepskiy.trenninggpsopenstreetmap.db

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.TrackItemBinding

class TrackAdapter(private val listener: Listener) :
    ListAdapter<TrackItem, TrackAdapter.Holder>(Comporator()) {
    class Holder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private var tempTrackItem: TrackItem? = null
        private val binding = TrackItemBinding.bind(view)

        init {
            binding.btDelete.setOnClickListener(this)
            binding.item.setOnClickListener(this)

        }

        @SuppressLint("SetTextI18n")
        fun bind(trackItem: TrackItem) = with(binding) {
            tvDate.text = trackItem.date
            tvSpeed.text = "Speed - ${trackItem.velocity} km/h"
            tvTime.text = "Time ${trackItem.time} s"
            tvDistance.text = "${trackItem.distance} km"
            tempTrackItem = trackItem
        }


        override fun onClick(p0: View) {
            val type = when (p0.id) {
                R.id.item -> ClickType.OPEN
                R.id.btDelete -> ClickType.DELETE

                else -> {
                    ClickType.OPEN
                }
            }
            tempTrackItem?.let { listener.onClick(it, type) }

        }
    }

    interface Listener {
        fun onClick(trackItem: TrackItem, type: ClickType)
    }

    class Comporator : DiffUtil.ItemCallback<TrackItem>() {
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    enum class ClickType {
        DELETE, OPEN
    }
}