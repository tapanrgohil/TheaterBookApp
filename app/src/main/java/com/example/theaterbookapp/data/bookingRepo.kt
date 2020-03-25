package com.example.theaterbookapp.data

import com.example.theaterbookapp.data.core.BaseRS
import com.example.theaterbookapp.data.core.BaseRepository
import com.example.theaterbookapp.data.core.Either
import com.example.theaterbookapp.data.core.MyException
import com.example.theaterbookapp.presentation.booking.Seat
import com.example.theaterbookapp.presentation.booking.SeatAdapterModel
import com.example.theaterbookapp.presentation.booking.Zone
import retrofit2.http.GET
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName


interface BookingRepo {
    suspend fun getBookingData(id: String): Either<MyException, BookingDataRS>
}

class BookingRepoImpl(private val bookingNetworkService: BookingNetworkService) : BaseRepository(),
    BookingRepo {
    override suspend fun getBookingData(id: String): Either<MyException, BookingDataRS> {
        return either {
            bookingNetworkService.getBookings(id).data
        }
    }

}

interface BookingNetworkService {

    @GET("bins/{id}")
    suspend fun getBookings(@Path("id") id: String): BaseRS<BookingDataRS>
}


data class BookingDataRS(
    @SerializedName("zones")
    val zones: List<ZoneRS>
)


data class ZoneRS(
    @SerializedName("colorCode")
    val colorCode: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("maxColumns")
    val maxColumns: Int,
    @SerializedName("maxRows")
    val maxRows: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("seats")
    val seats: List<SeatRs>,
    @SerializedName("seatsBooked")
    val seatsBooked: Int,
    @SerializedName("sort")
    val sort: Int,
    @SerializedName("startIndex")
    val startIndex: Int,
    @SerializedName("totalSeats")
    val totalSeats: Int
) {
    fun toZone() = Zone(
        colorCode,
        id,
        maxColumns,
        maxRows,
        name,
        price,
        mapSeatsToAdapterModel(),
        seatsBooked,
        sort,
        startIndex,
        totalSeats
    )

    private fun mapSeatsToAdapterModel(): List<List<SeatAdapterModel>> {
        val rows = arrayListOf<ArrayList<SeatAdapterModel>>()


        for (rowNumber in 0 until maxRows) {
            val row = arrayListOf<SeatAdapterModel>()
            val seatsInRow = seats.filter {
                it.rowNumber == rowNumber + 1
            }

            for (colNumber in 0 until maxColumns) {
                val seatAtIndex = seatsInRow.find { it.columnIndex == colNumber + 1 }
                if (seatAtIndex != null) {
                    row.add(SeatAdapterModel.SeatView(seatAtIndex.toSeat(), rowNumber, colNumber))
                } else {
                    row.add(SeatAdapterModel.EmptyView(rowNumber, colNumber))
                }
            }
            rows.add(row)
        }
        return rows
    }
}

data class SeatRs(
    @SerializedName("bookedStatus")
    val bookedStatus: Int,
    @SerializedName("columnIndex")
    val columnIndex: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rowNumber")
    val rowNumber: Int,
    @SerializedName("seatKey")
    val seatKey: String
) {

    fun toSeat() = Seat(bookedStatus, columnIndex, id, name, rowNumber, seatKey)
}