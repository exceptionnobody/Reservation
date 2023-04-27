package it.polito.g13.businesslogic

import androidx.lifecycle.LiveData
import it.polito.g13.dao.PosresDao
import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import java.util.Date
import javax.inject.Inject

class BusinessClass
@Inject constructor(
    private val reservationDao: ReservationDao,
    private val posresDao: PosresDao,

    ) {

    /* For the viewModel */
    fun getAllReservations() : LiveData<List<Reservation>> {
        return reservationDao.gettAllReservations()
    }

    fun inserReservation(reservation: Reservation) {
        if(!reservationDao.isPresentAReservation(reservation.id))
            reservationDao.inserReservation(reservation)
    }


    fun getSingleReservation(id:Long) : LiveData<Reservation> {
       return reservationDao.getSingleReservation(id)
    }

    fun getAllPosRes() :  LiveData<List<PosRes>> {
        return posresDao.getAllPosRes()
    }

    fun updatePosRes(pos:PosRes){
        posresDao.updatePosRes(pos)
    }

    fun insertNewPos(id: Int, name: String){
        if(!posresDao.isPresent(id))
            posresDao.insertPosRes(PosRes(id, name, 1,"calcio", Date(), true))
    }

    fun updateDatePosRes(id: Int, newdate: Date ){
        if(posresDao.isPresent(id)){
            val updatePosRes = posresDao.getSinglePosRes(id)
            updatePosRes.data = newdate
            posresDao.updatePosRes(updatePosRes)
        }
    }

    private fun isAReservationPresent(id:Int) : Boolean {
        return reservationDao.isPresentAReservation(id)
    }

    private fun getASingleReservation(id:Long) : Reservation {
       return reservationDao.getASingleReservation(id)
    }

     private fun changeReservation( reservation: Reservation) {
        return reservationDao.updateReservation( reservation)
    }


}