package it.polito.g13.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.Reservation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val reservationDao: ReservationDao) : ViewModel() {
    private val _reservations = MutableLiveData<List<Reservation>>()

    val reservations: LiveData<List<Reservation>> = _reservations

    fun getAllReservations() {
        _reservations.postValue(reservationDao.gettAllReservations())
    }
}