package com.virtaapps.mobile.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.virtaapps.mobile.base.BaseAdapter
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.databinding.ItemHistoryBinding

class HistoryAdapter : BaseAdapter<ItemHistoryBinding, MeasurementHistory>() {
    override val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> ItemHistoryBinding =
        ItemHistoryBinding::inflate

    override fun bind(binding: ItemHistoryBinding, item: MeasurementHistory) {
        with(binding) {
            tvName.text = item.name
            tvStandart.text = item.ukuran
            Picasso.get().load(item.gambar).into(imgHistory)
        }
    }
}