package it.polito.g13.businesslogic

import android.util.Log
import androidx.lifecycle.LiveData
import it.polito.g13.dao.PosresDao
import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessClass
@Inject constructor(
    private val reservationDao: ReservationDao,
    private val posresDao: PosresDao,

    ) {

    /* For the viewModel */
    fun getAllReservations() : LiveData<List<Reservation>> {
        return reservationDao.getAllReservations()
    }

    fun insertReservation(reservation: Reservation) {
        if(!reservationDao.isPresentAReservation(reservation.id))
            reservationDao.insertReservation(reservation)
    }


    fun getSingleReservation(id:Long) : Reservation {
       return reservationDao.getSingleReservation(id)
    }

    fun getAllPosRes() :  LiveData<List<PosRes>> {
        return posresDao.getAllPosRes()
    }

    fun updatePosRes(pos:PosRes){
        posresDao.updatePosRes(pos)
    }

    fun insertNewPos(posRes: PosRes){
        if(!posresDao.isPresent(posRes.id))
            posresDao.insertPosRes(posRes)
    }

    fun updateDatePosRes(id: Int, newdate: Date ){
        if(posresDao.isPresent(id)){
            val updatePosRes = posresDao.getSinglePosRes(id)
            updatePosRes.data = newdate
            posresDao.updatePosRes(updatePosRes)
        }
    }

    fun deleteReservation(reservation: Reservation) {
        if(reservationDao.isPresentAReservation(reservation.id))
            reservationDao.removeReservation(reservation)
    }

    private fun isAReservationPresent(id:Int) : Boolean {
        return reservationDao.isPresentAReservation(id)
    }

    fun getASingleReservation(id:Int) : Reservation {
       return reservationDao.getASingleReservation(id)
    }

     fun changeReservation( idReservation: Int, newData: Date) {
         updateReservation(idReservation, newData)
    }

    private fun updateReservation(idReservation: Int, newData: Date) {
        if(reservationDao.isPresentAReservation(idReservation)){
            val old_res = reservationDao.getASingleReservation(idReservation)
            old_res.data = newData
             reservationDao.updateReservation(old_res)

        }
    }

    fun getPosResSportDate(sport: String, date: Date) : List<PosRes> {
        return posresDao.getPosResSportDate(sport, date)
    }
}