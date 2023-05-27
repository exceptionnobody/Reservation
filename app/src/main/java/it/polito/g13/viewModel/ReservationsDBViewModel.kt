package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import java.util.Date

import javax.inject.Inject


class ReservationsDBViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _reservations = MutableLiveData<List<MutableMap<String, Any>>>()
    val reservations: LiveData<List<MutableMap<String, Any>>> = _reservations

    private val _listReservationsByDate = MutableLiveData<List<MutableMap<String, Any>>>()
    val listReservationsByDate: LiveData<List<MutableMap<String, Any>>> = _listReservationsByDate

    private val _singleReservation = MutableLiveData<MutableMap<String, Any>>()
    val singleReservation: LiveData<MutableMap<String, Any>> = _singleReservation

    private val user = FirebaseAuth.getInstance().currentUser

    init {

        if (user != null && user.email != null) {
            val reservationsRef = db.collection("users").document(user.email!!).collection("reservations")

            reservationsRef
                .get()
                .addOnSuccessListener { listReservations ->
                    val allReservations: MutableList<MutableMap<String, Any>> = mutableListOf()

                    for (reservation in listReservations) {
                        val reservationData = reservation.data
                        val idStruct = reservationData["idstruttura"] as DocumentReference

                        reservationData["idprenotazione"] = reservation.id

                        idStruct
                            .get()
                            .addOnSuccessListener {
                                reservationData["nomestruttura"] = it.data?.get("nomestruttura")
                                allReservations.add(reservationData)
                            }
                            .addOnFailureListener {
                                allReservations.add(reservationData)
                            }

                    }

                    _reservations.value = allReservations
                }
        }
    }

    fun insertReservation(posresid : String, idstruttura : Any?, data: Date, idcampo: Any?, tiposport: String, note: String) {
        if (user != null && user.email != null) {
            val reservationsRef = db.collection("users").document(user.email!!).collection("reservations")
            val data = hashMapOf(
                "posresid" to posresid.trim(),
                "idstruttura" to idstruttura,
                "data" to data,
                "idcampo" to idcampo,
                "tiposport" to tiposport,
                "note" to note,
                "activeflag" to true,
            )

            reservationsRef
                .add(data)
        }
    }

    /*fun updateReservation(idReservation: Int, newData: Date, notes: String) {
        businessLogic.changeReservation(idReservation, newData, notes)
    }

    fun deleteReservation(reservation: Reservation) {
        businessLogic.deleteReservation(reservation)
    }

    fun getSingleReservation(idReservation: Int) {
        _singleReservation.postValue(businessLogic.getASingleReservation(idReservation))
    }

    fun getReservationsByDate(date: Date) {
        _listReservationsByDate.postValue(businessLogic.getReservationsByDate(date))
    }*/
}