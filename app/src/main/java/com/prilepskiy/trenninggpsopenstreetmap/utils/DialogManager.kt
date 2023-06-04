package com.prilepskiy.trenninggpsopenstreetmap.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.prilepskiy.trenninggpsopenstreetmap.R
import com.prilepskiy.trenninggpsopenstreetmap.databinding.SaveDialogBinding
import com.prilepskiy.trenninggpsopenstreetmap.db.TrackItem

object DialogManager {
    fun showLocEnabledDialog(context: Context,listener: Listener){
        val builder=AlertDialog.Builder(context)
        val dialog=builder.create()
        dialog.setTitle(R.string.location_disabled)
        dialog.setMessage(context.getString(R.string.location_dialog_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes"){
            _,_->
            listener.onClick()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No"){
                _,_-> dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun showSaveDialog(context: Context, item: TrackItem?, listener: Listener){
        val builder=AlertDialog.Builder(context)
        val binding=SaveDialogBinding.inflate(LayoutInflater.from(context),null,false)
        builder.setView(binding.root)
        val dialog=builder.create()
        binding.apply {
            tvTime.text = "Time: ${item?.time} s"
            tvDictance.text = "Dictance: ${item?.distance}km/h"
            tvSpeed.text ="Velocity ${item?.velocity}km"

            bSave.setOnClickListener {
                listener.onClick()
                dialog.dismiss()
            }
            bCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    interface Listener{
        fun onClick()
    }
}