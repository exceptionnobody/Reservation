package it.polito.g13.businesslogic

import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.Reservation
import javax.inject.Inject

class BusinessClass
@Inject constructor(
    private val dao: ReservationDao,
) {

    fun inserReservation(reservation: Reservation) = dao.inserReservation(reservation)
    fun getAllReservations() = dao.gettAllReservations()
    fun getSingleReservation(id:Long) = dao.getSingleReservation(id)

    fun getASingleReservation(id:Long) = dao.getASingleReservation(id)
    fun isAReservationPresent(id:Long) : Boolean {

        return dao.isPresentAReservation(id) != 0
    }

    fun changeReservation( reservation: Reservation) = dao.updateReservation( reservation)

}