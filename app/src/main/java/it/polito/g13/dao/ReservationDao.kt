package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.Reservation
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import java.util.Date

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserReservation(noteEntity: Reservation)

    @Query("SELECT * FROM $RESERVATION_TABLE where id == :id")
    fun getSingleReservation(id: Long) : Reservation

    @Query("SELECT * FROM $RESERVATION_TABLE where id == :id")
    fun getASingleReservation(id: Int) : Reservation

    @Query("SELECT * FROM $RESERVATION_TABLE")
    fun gettAllReservations() : LiveData<List<Reservation>>

    @Query("SELECT * FROM $RESERVATION_TABLE WHERE date = :targetDate")
    fun findReservationsOnDate(targetDate: Date): List<Reservation>

    @Query("SELECT count(1) FROM $RESERVATION_TABLE WHERE id == :id")
    fun isPresentAReservation(id: Int) : Boolean

    @Update
    fun updateReservation(reservation: Reservation)

    @Delete
    fun removeReservation(reservation: Reservation)


}