package com.example.theaterbookapp.presentation.booking.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.theaterbookapp.R
import com.example.theaterbookapp.presentation.booking.Seat
import com.example.theaterbookapp.presentation.booking.SeatAdapterModel
import com.example.theaterbookapp.presentation.booking.Zone
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.item_seat_row.view.*

/**
 * Adapter for seat rows inside the zone
 */
class SeatRowAdapter(
    private val maxRows: Int,
    private val maxCols: Int,
    private val list: List<List<SeatAdapterModel>> = arrayListOf(),
    private val onClickListener: (SeatAdapterModel) -> Unit
) :
    RecyclerView.Adapter<SeatRowAdapter.SeatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_seat_row,
                parent,
                false
            ),
            onClickListener
        )
    }

    override fun getItemCount() = maxRows

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.setData(list[position])
    }

    inner class SeatViewHolder(view: View, private val onClickListener: (SeatAdapterModel) -> Unit) :
        RecyclerView.ViewHolder(view) {

        fun setData(seats: List<SeatAdapterModel>) {
            itemView.apply {
                rvSeatsRow.apply {
                    adapter = SeatAdapter(seats, onClickListener)
                    layoutManager =
                        FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP)
                }
            }
        }
    }

}