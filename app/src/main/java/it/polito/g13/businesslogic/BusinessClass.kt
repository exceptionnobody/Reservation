package it.polito.g13.businesslogic

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.g13.dao.CampoDao
import it.polito.g13.dao.PosresDao
import it.polito.g13.dao.ReservationDao
import it.polito.g13.dao.ReviewStructDao
import it.polito.g13.dao.SportsDao
import it.polito.g13.dao.StructureDao
import it.polito.g13.dao.UserDao
import it.polito.g13.entities.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessClass
@Inject constructor(
    private val reservationDao: ReservationDao,
    private val posresDao: PosresDao,
    private val userDao: UserDao,
    private val strutDao: StructureDao,
    private val campoDao: CampoDao,
    private val sportsDao: SportsDao,
    private val reviewStructDao: ReviewStructDao,

    ) {
    private val db: FirebaseFirestore = Firebase.firestore
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
        pos.flag=!pos.flag
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

    fun getReservationsByDate(date: Date) : List<Reservation> {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
        return reservationDao.findReservationsOnDate(formattedDate)
    }

     fun changeReservation( idReservation: Int, newData: Date, notes: String) {
         updateReservation(idReservation, newData, notes)
    }

    private fun updateReservation(idReservation: Int, newData: Date, notes: String) {
        if(reservationDao.isPresentAReservation(idReservation)){
            val old_res = reservationDao.getASingleReservation(idReservation)
            old_res.data = newData
            old_res.note = notes
             reservationDao.updateReservation(old_res)

        }
    }

    fun getPosResSportDate(sport: String, date: Date) : List<PosRes> {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
        return posresDao.getPosResSportDate(sport, formattedDate)
    }

    fun getPosResSportTime(sport: String, from: String, to: String) : List<PosRes> {
        return posresDao.getPosResSportTime(sport, from, to)
    }

    fun getPosResBySport(sport: String) : List<PosRes> {
        return posresDao.getPosResBySport(sport)
    }

    fun getPosResSportById(posResId: Int) : PosRes {
        return posresDao.getSinglePosRes(posResId)
    }

    fun getPosResSportDateAndStruct(sport: String, date: Date, struct: String) : List<PosRes> {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
        return posresDao.getPosResSportDateAndStruct(sport, formattedDate, struct)
    }

    fun getPosResStructureSportDateAndTime(sport: String, date: Date, from: String, to: String, struct: String) : List<PosRes> {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
        return posresDao.getPosResStructureSportDateAndTime(sport, formattedDate, from, to, struct)
    }

    fun createReview(review: review_struct) {
        return reviewStructDao.insertReviewStructure(review)
    }

    fun updateReview(review: review_struct) {
        return reviewStructDao.updateReviewStructure(review)
    }

    fun getReviewByStructureAndUserId(structureId: Int, userId: Int): review_struct {
        return reviewStructDao.getSingleRevStructByUserAndStructureId(structureId, userId)
    }

    fun getReviewById(reviewId: Int): review_struct {
        return reviewStructDao.getSingleRevStruct(reviewId)
    }

    fun getAllStructures() : LiveData<List<Struttura>> {
        return strutDao.getaLLStructure()
    }

    fun insertStructure(structure: Struttura) {
        if (!strutDao.isPresent(structure.id))
            strutDao.insertStructure(structure)
    }

    fun changeUserInfo(user: User) {
        userDao.updateUser(user)

    }
    fun getUserById(id:Int): User {
        return userDao.getSingleUser(id)
    }

    fun insertUser(user: User){
        db.collection("users")
            .document(user.mail)
            .collection("profiles")
            .document(user.nickname)
            .set(user)
            .addOnSuccessListener {
                true
            }
            .addOnFailureListener{
                false
            }

    }

    fun getSportsById(id: Int): Sports? {
        return sportsDao.getUserSportsByIdUser(id)
    }

    fun changeSports(sports: Sports) {
        return sportsDao.updateSports(sports)
    }

    fun  insertSports(sports: Sports){
        return sportsDao.insertSports(sports)
    }

}