package it.polito.g13.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val repository: BusinessClass,
) : ViewModel() {

    private val _reservations = MutableLiveData<List<Reservation>>().also { it.value = emptyList() }
    val reservations: LiveData<List<Reservation>> = _reservations

    private val _singleReservation = MutableLiveData<Reservation>()
    val singleReservation: LiveData<Reservation> = _singleReservation

    fun getAllReservations() {
        _reservations.postValue(repository.getAllReservations())
    }

    fun insertReservation(reservation: Reservation) {
        repository.inserReservation(reservation)
    }

    fun getSingleReservation(id: Long) {
        _singleReservation.postValue(repository.getSingleReservation(id))
    }

    fun reservationIsPresent(id: Long) : Boolean {
        return repository.isAReservationPresent(id)
    }

    fun updateReservation(reservation: Reservation) {
        repository.changeReservation(reservation)
        _singleReservation.postValue(repository.getSingleReservation(reservation.id.toLong()))
    }
}