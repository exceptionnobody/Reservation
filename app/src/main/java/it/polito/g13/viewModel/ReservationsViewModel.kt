package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import java.util.Date

import javax.inject.Inject


@HiltViewModel
class ReservationsViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    val _listReservations = MutableLiveData<List<Reservation>>()
    val listReservations: LiveData<List<Reservation>> = _listReservations

    private val _singleReservation = MutableLiveData<Reservation>()
    val singleReservation: LiveData<Reservation> = _singleReservation

    fun insertReservation(reservation: Reservation) {
        businessLogic.insertReservation(reservation)
    }

    fun updateReservation(idReservation: Int, newData: Date) {
        businessLogic.changeReservation(idReservation, newData)
    }

    fun deleteReservation(reservation: Reservation) {
        businessLogic.deleteReservation(reservation)
    }

    fun getSingleReservation(idReservation: Int) {
        _singleReservation.postValue(businessLogic.getASingleReservation(idReservation))
    }

    fun getReservationsByDate(date: Date) {
        _listReservations.postValue(businessLogic.getReservationsByDate(date))
    }
}