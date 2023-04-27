package it.polito.g13.vieModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation

import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    val reservations: LiveData<List<Reservation>> = businessLogic.getAllReservations()

    fun insertReservation(reservation: Reservation) {
        businessLogic.inserReservation(reservation)
    }

    fun updateReservation(reservation: Reservation) {
        businessLogic.changeReservation(reservation)
    }

    fun deleteReservation(reservation: Reservation) {
        businessLogic.deleteReservation(reservation)
    }

}