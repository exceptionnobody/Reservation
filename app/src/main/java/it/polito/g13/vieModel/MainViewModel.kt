package it.polito.g13.vieModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import java.util.Date

import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    val reservations: LiveData<List<Reservation>> = businessLogic.getAllReservations()

    fun changeReservation(id: Int, date: Date, sport: String) {
        businessLogic.changeReservation(Reservation(id, date, sport))
    }

    fun insertReservation(reservation: Reservation) {
        businessLogic.inserReservation(reservation)
    }

    fun reservationIsPresent(id: Long) : Boolean {
        return businessLogic.isAReservationPresent(id)
    }

    fun updateReservation(reservation: Reservation) {
        businessLogic.changeReservation(reservation)
    }

}