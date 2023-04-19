package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.g13.entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM reservations")
    fun gettAllReservations() : LiveData<List<Reservation>>
}