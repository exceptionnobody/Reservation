package it.polito.g13.businesslogic

import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.Reservation
import javax.inject.Inject

class BusinessClass
@Inject constructor(
    private val dao: ReservationDao,
) {

    fun saveReservation(reservation: Reservation) = dao.inserReservation(reservation)
}