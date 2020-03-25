package com.example.theaterbookapp.presentation.booking.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.theaterbookapp.R
import com.example.theaterbookapp.presentation.booking.SeatAdapterModel
import kotlinx.android.synthetic.main.item_seat.view.*

/**
 * Adapter for individual seats
 */
class SeatAdapter(val list: List<SeatAdapterModel> = arrayListOf(), val onClick: (SeatAdapterModel) -> Unit) :
    RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_seat,
                parent,
                false
            ), onClick
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.setData(list[position])
    }

    inner class SeatViewHolder(view: View, val onClick: (SeatAdapterModel) -> Unit) :
        RecyclerView.ViewHolder(view) {
        var seatView: SeatAdapterModel.SeatView? = null

        init {
            view.setOnClickListener {
                seatView?.apply {
                    onClick(this)
                }
            }

        }

        fun setData(seat: SeatAdapterModel) {
            if (seat is SeatAdapterModel.SeatView) {
                this@SeatViewHolder.seatView = seat
                setupImages()
            } else {
                this@SeatViewHolder.seatView = null

            }

        }

        private fun setupImages() {
            itemView.apply {
                if (seatView?.seat?.isSelected == true) {
                    ivSeat.setImageResource(R.drawable.selected_seat)
                } else {
                    if (seatView?.seat?.bookedStatus == 1) {
                        ivSeat.setImageResource(R.drawable.sold_seat)
                    } else {
                        ivSeat.setImageResource(R.drawable.available_seat)
                    }
                }
            }
        }
    }

}