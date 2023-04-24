package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.polito.g13.entities.Reservation
import java.util.Date

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserReservation(noteEntity: Reservation)
    @Query("SELECT * FROM reservations")
    fun gettAllReservations() : LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE date = :targetDate")
    fun findReservationsOnDate(targetDate: Date): List<Reservation>
}