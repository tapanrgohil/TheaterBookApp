package com.example.theaterbookapp.presentation.booking

import com.example.theaterbookapp.data.SeatRs
import com.google.gson.annotations.SerializedName

class Seat(
    val bookedStatus: Int,
    val columnIndex: Int,
    val id: Int,
    val name: String,
    val rowNumber: Int,
    val seatKey: String
) {
    var isSelected = false
}


data class Zone(
    val colorCode: String,
    val id: Int,
    val maxColumns: Int,
    val maxRows: Int,
    val name: String,
    val price: Int,
    var seats: List<List<SeatAdapterModel>>,
    val seatsBooked: Int,
    val sort: Int,
    val startIndex: Int,
    val totalSeats: Int
)

/**
 * Models for inner seat adapters
 */
sealed class SeatAdapterModel(val rowNumber: Int, val colNumber: Int) {
    class SeatView(val seat: Seat, rowNumber: Int, colNumber: Int) :
        SeatAdapterModel(rowNumber, colNumber)

    class EmptyView(rowNumber: Int, colNumber: Int) : SeatAdapterModel(rowNumber, colNumber)

}


/**
 * Models for main adapter
 */
sealed class MainAdapterModel() {
    class ZoneView(val zone: Zone) : MainAdapterModel()
    class EmptyView() : MainAdapterModel()
}