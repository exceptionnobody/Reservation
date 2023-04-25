package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.polito.g13.entities.Reservation
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import java.util.Date

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserReservation(noteEntity: Reservation)
    @Query("SELECT * FROM $RESERVATION_TABLE")
    fun gettAllReservations() : LiveData<List<Reservation>>

    @Query("SELECT * FROM $RESERVATION_TABLE WHERE date = :targetDate")
    fun findReservationsOnDate(targetDate: Date): List<Reservation>
}