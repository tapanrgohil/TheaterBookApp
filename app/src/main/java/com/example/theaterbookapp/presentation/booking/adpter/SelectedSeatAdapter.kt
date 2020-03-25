package com.example.theaterbookapp.presentation.booking.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
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
import kotlinx.android.synthetic.main.item_selected_list.view.*

/**
 * To show chips of selected list on activity with DiffUtil to animate
 */
class SelectedSeatAdapter :
    RecyclerView.Adapter<SelectedSeatAdapter.SeatViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<Seat>() {
        override fun areItemsTheSame(oldItem: Seat, newItem: Seat): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Seat, newItem: Seat): Boolean {
            return (oldItem.id == newItem.id)
        }

    }
    val differ: AsyncListDiffer<Seat> = object : AsyncListDiffer<Seat>(this, diffCallBack) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_selected_list,
                parent,
                false
            )
        )
    }

    fun setList(list: MutableList<Seat>) {
        differ.submitList(list)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    inner class SeatViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun setData(seats: Seat) {
            itemView.apply {
                chip.text = seats.name
            }
        }
    }

}

