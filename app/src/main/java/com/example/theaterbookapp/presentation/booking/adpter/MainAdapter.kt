package com.example.theaterbookapp.presentation.booking.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.theaterbookapp.R
import com.example.theaterbookapp.presentation.booking.MainAdapterModel
import com.example.theaterbookapp.presentation.booking.Seat
import com.example.theaterbookapp.presentation.booking.SeatAdapterModel
import com.example.theaterbookapp.presentation.booking.Zone
import kotlinx.android.synthetic.main.item_zone.view.*

/**
 * Main Adapter for zones and space between zones
 */
class MainAdapter(
    private val onClickListener: (Zone, SeatAdapterModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<MainAdapterModel>()

    fun setData(list: List<MainAdapterModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.getOrNull(position) is MainAdapterModel.ZoneView) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            MainViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_zone,
                    parent,
                    false
                ), onClickListener
            )
        } else {
            SpaceViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_space,
                    parent,
                    false
                )
            )//TO add some space between to zones
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list.get(position)
        if (model is MainAdapterModel.ZoneView && holder is MainViewHolder) {
            holder.setData(model.zone)
        }
    }



    class MainViewHolder(
        view: View, private val onClickListener: (Zone, SeatAdapterModel) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        fun setData(zone: Zone) {
            itemView?.apply {
                rvSeats.apply {
                    adapter = SeatRowAdapter(zone.maxRows, zone.maxColumns, zone.seats) {
                        onClickListener(zone, it)
                    }
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    class SpaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}