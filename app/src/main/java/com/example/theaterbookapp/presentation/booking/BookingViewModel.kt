package com.example.theaterbookapp.presentation.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.theaterbookapp.R
import com.example.theaterbookapp.data.BookingRepo
import com.example.theaterbookapp.data.core.MyException
import com.example.theaterbookapp.data.core.map
import com.example.theaterbookapp.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class BookingViewModel : BaseViewModel() {
    private val theaterId = "17b7rd"
    private val bookingRepo by inject<BookingRepo>()

    private val selectedSeats = HashMap<Int, MutableList<Seat>>()

    private val bookingLiveData by lazy { MutableLiveData<List<Zone>>() } // data from api
    //when selects and seats add to list and used to update ui
    private val selectedSeatLiveData by lazy { MutableLiveData<List<Seat>>() }

    /**
     * Api calling
     */
    fun getBookingDetails() {
        postData(bookingLiveData) {
            bookingRepo.getBookingData(theaterId)
                .map {
                    it.zones.map { it.toZone() }
                }
        }
    }

    //Cast mutable to immutable so live data do not exposed to presentation layer
    fun getBookingLiveData() = (bookingLiveData as LiveData<List<Zone>>)

    fun getSelectedSeatLiveData() = (selectedSeatLiveData as LiveData<List<Seat>>)

    /**
     * Validations on selecting any seat
     * //only 3 seat validation for zone 0 (id =5)
     * //only 3 seat validation for zone 1 (id =6)
     */
    fun onSeatSelect(zone: Zone, seatModel: SeatAdapterModel): Boolean {
        if (seatModel !is SeatAdapterModel.SeatView) {
            return false
        }
        val seat = seatModel.seat
        val selectedZone = selectedSeats.get(zone.id)
        return if (seat.bookedStatus == 1) {
            errorLiveData.postValue(
                MyException.ValidationException(
                    RuntimeException("Clicked on booked seat"),
                    R.string.sold_seat
                )
            )
            false
        } else if (selectedZone == null) {
            selectedSeats[zone.id] = arrayListOf(seat)
            true
        } else {
            val selectedEntry = selectedZone.find { it.id == seat.id }
            if (zone.id == 5 && selectedZone.size >= 3 && selectedEntry == null) { //only 3 seat validation for zone 0 (id =5)
                errorLiveData.postValue(
                    MyException.ValidationException(
                        RuntimeException(""),
                        R.string.maximum_seat_selected
                    )
                )
                false
            } else if (zone.id == 6 && selectedZone.size >= 4 && selectedEntry == null) { //only 3 seat validation for zone 1 (id =6)
                errorLiveData.postValue(
                    MyException.ValidationException(
                        RuntimeException(""),
                        R.string.maximum_seat_selected
                    )
                )
                false
            } else if (selectedEntry == null) {
                selectedZone.add(seat)
                selectedSeats[zone.id] = selectedZone
                true
            } else {
                selectedZone.removeAll { it.id == seat.id }
                selectedSeats[zone.id] = selectedZone
                true
            }
        }
    }

    /**
     * update list to notify recyclerview again
     */
    fun updateList(zone: Zone, seat: SeatAdapterModel) {
        if (seat is SeatAdapterModel.SeatView) {// Nothing for empty view
            viewModelScope.launch(Dispatchers.IO) {
                //Work on background thread
                if (zone.id == 5) { // 3 selection for zone 5
                    val autoSelected = getAutoSelectSeats(zone, seat)
                    selectedSeats[zone.id] =
                        autoSelected.map { (it as SeatAdapterModel.SeatView).seat }.toMutableList()

                    val autoSelectId = autoSelected.map { it as SeatAdapterModel.SeatView }
                        .map {
                            it.seat.id
                        }
                    bookingLiveData.value.orEmpty().map { zoneLoop ->
                        zoneLoop.seats = zoneLoop.seats.asSequence().map { it ->
                            it.map { seatAdapterModel ->
                                if (seatAdapterModel is SeatAdapterModel.SeatView) {
                                    seatAdapterModel.seat.isSelected =
                                        autoSelectId.contains(seatAdapterModel.seat.id)
                                }
                                seatAdapterModel
                            }
                            it
                        }.toList()
                        zoneLoop
                    }.apply {
                        bookingLiveData.postValue(this)
                    }
                } else {
                    bookingLiveData.value.orEmpty().map { zoneLoop ->
                        zoneLoop.seats = zoneLoop.seats.asSequence().map {
                            it.map { seatAdapterModel ->
                                if (seatAdapterModel is SeatAdapterModel.SeatView
                                    && seatAdapterModel.seat.id == seat.seat.id
                                ) {
                                    seatAdapterModel.seat.isSelected =
                                        !seatAdapterModel.seat.isSelected
                                }
                                seatAdapterModel
                            }
                            it
                        }.toList()
                        zoneLoop
                    }.apply {
                        bookingLiveData.postValue(this)
                    }
                }

                val seats = arrayListOf<Seat>()
                selectedSeats.forEach {
                    seats.addAll(it.value)
                }
                selectedSeatLiveData.postValue(seats)
            }
        }

    }

    /**
     * 3 Seat selection for zone 0 (Zone id 5)
     */
    private fun getAutoSelectSeats(
        zone: Zone,
        seat: SeatAdapterModel
    ): ArrayList<SeatAdapterModel> {
        val autoSelectSeats = arrayListOf<SeatAdapterModel>()
        val seats = bookingLiveData.value.orEmpty().find { it.id == zone.id }
            ?.seats.orEmpty()
        val sameRowSeats = seats[seat.rowNumber]
        val frontRows = if (seat.rowNumber > 0) {
            seats[seat.rowNumber - 1]
        } else {
            arrayListOf()
        }
        val backRows = if (seat.rowNumber < zone.maxRows - 1) {
            seats[seat.rowNumber + 1]
        } else {
            arrayListOf()
        }
        for (i in seat.colNumber..zone.maxColumns - 1) {
            val checkSeat = sameRowSeats[i]
            if (checkSeat !is SeatAdapterModel.SeatView || checkSeat.seat.bookedStatus == 1) {
                break
            } else if (checkSeat.seat.isSelected) {
                return arrayListOf()
            } else {
                autoSelectSeats.add(checkSeat)
                if (autoSelectSeats.size == 3) {
                    return autoSelectSeats
                }
            }
        }

        if (seat.colNumber > 1) {
            for (i in seat.colNumber - 1 downTo 0) {
                val checkSeat = sameRowSeats[i]
                if (checkSeat !is SeatAdapterModel.SeatView || checkSeat.seat.bookedStatus == 1) {
                    break
                } else if (!checkSeat.seat.isSelected) {
                    autoSelectSeats.add(checkSeat)
                    if (autoSelectSeats.size == 3) {
                        return autoSelectSeats
                    }
                }
            }
        }
        if (frontRows.isNotEmpty()) {
            for (i in seat.colNumber..zone.maxColumns - 1) {
                val checkSeat = frontRows[i]
                if (checkSeat !is SeatAdapterModel.SeatView || checkSeat.seat.bookedStatus == 1) {
                    break
                } else if (!checkSeat.seat.isSelected) {
                    autoSelectSeats.add(checkSeat)
                    if (autoSelectSeats.size == 3) {
                        return autoSelectSeats
                    }
                }
            }

            if (seat.colNumber > 1) {
                for (i in seat.colNumber - 1 downTo 0) {
                    val checkSeat = frontRows[i]
                    if (checkSeat is SeatAdapterModel.EmptyView) {
                        break
                    } else if (checkSeat is SeatAdapterModel.SeatView && !checkSeat.seat.isSelected && checkSeat.seat.bookedStatus == 0) {
                        autoSelectSeats.add(checkSeat)
                        if (autoSelectSeats.size == 3) {
                            return autoSelectSeats
                        }
                    }
                }

            }
        }

        if (backRows.isNotEmpty()) {
            for (i in seat.colNumber..zone.maxColumns - 1) {
                val checkSeat = backRows[i]
                if (checkSeat !is SeatAdapterModel.SeatView || checkSeat.seat.bookedStatus == 1) {
                    break
                } else if (!checkSeat.seat.isSelected) {
                    autoSelectSeats.add(checkSeat)
                    if (autoSelectSeats.size == 3) {
                        return autoSelectSeats
                    }
                }
            }

            if (seat.colNumber > 1) {
                for (i in seat.colNumber - 1 downTo 0) {
                    val checkSeat = backRows[i]
                    if (checkSeat !is SeatAdapterModel.SeatView || checkSeat.seat.bookedStatus == 1) {
                        break
                    } else if (!checkSeat.seat.isSelected) {
                        autoSelectSeats.add(checkSeat)
                        if (autoSelectSeats.size == 3) {
                            return autoSelectSeats
                        }
                    }
                }

            }
        }

        return autoSelectSeats
    }

}