package it.polito.g13.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import javax.inject.Inject


class ReservationsDBViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _reservations = MutableLiveData<List<MutableMap<String, Any>>>()
    val reservations: LiveData<List<MutableMap<String, Any>>> = _reservations

    private val _listReservationsByDate = MutableLiveData<List<MutableMap<String, Any>>>()
    val listReservationsByDate: LiveData<List<MutableMap<String, Any>>> = _listReservationsByDate

    private val _userReservations = MutableLiveData<List<MutableMap<String, Any>>>()
    val userReservations: LiveData<List<MutableMap<String, Any>>> = _userReservations

    private val _userHasPastReservations = MutableLiveData<Boolean>()
    val userHasPastReservations: LiveData<Boolean> = _userHasPastReservations

    private val _singleReservation = MutableLiveData<MutableMap<String, Any?>>()
    val singleReservation: LiveData<MutableMap<String, Any?>> = _singleReservation

    private val user = FirebaseAuth.getInstance().currentUser

    init {

        if (user != null && user.uid != null) {
            val reservationsRef = db.collection("users").document(user.uid!!).collection("reservations")

            reservationsRef
                .get()
                .addOnSuccessListener { listReservations ->
                    val allReservations: MutableList<MutableMap<String, Any>> = mutableListOf()

                    for (reservation in listReservations) {
                        val reservationData = reservation.data

                        db
                            .collection("reservations")
                            .document(reservationData["posresid"].toString())
                            .get()
                            .addOnSuccessListener {
                                if (it.data?.get("idstruttura") != null && !it.data?.get("idstruttura").toString().isNullOrBlank() && !it.data?.get("idstruttura").toString().isNullOrEmpty()) {
                                    val idStruct = it.data?.get("idstruttura") as DocumentReference
                                    reservationData["tiposport"] = it.data?.get("tiposport")
                                    reservationData["data"] = it.data?.get("data")
                                    reservationData["reservationid"] = it.id

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
                            }
                    }

                    _reservations.value = allReservations
                }
        }
    }

    fun getUserPastReservations() {
        val reservationsRef = db
            .collection("users")
            .document(user?.uid!!)
            .collection("reservations")

        reservationsRef
            .get()
            .addOnSuccessListener { listReservations ->
                if (listReservations.isEmpty) {
                    _userHasPastReservations.value = false
                }
                else {
                    _userHasPastReservations.value = false

                    val allUserReservations: MutableList<MutableMap<String, Any>> = mutableListOf()

                    for (reservation in listReservations) {
                        val reservationData = reservation.data

                        db
                            .collection("reservations")
                            .document(reservationData["posresid"].toString())
                            .get()
                            .addOnSuccessListener {
                                if (it.data?.get("idstruttura") != null && !it.data?.get("idstruttura")
                                        .toString().isNullOrBlank() && !it.data?.get("idstruttura")
                                        .toString().isNullOrEmpty()
                                ) {

                                    val retrievedDate = it.data?.get("data") as Timestamp
                                    val seconds = retrievedDate.seconds
                                    val nanoseconds = retrievedDate.nanoseconds
                                    val milliseconds = seconds * 1000 + nanoseconds / 1_000_000

                                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(
                                        Date(milliseconds)
                                    )
                                    val formattedDate = SimpleDateFormat("yyyy-MM-dd").parse(date)
                                    val today =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date())
                                    val formattedToday = SimpleDateFormat("yyyy-MM-dd").parse(today)

                                    if (formattedDate.before(formattedToday)) {
                                        _userHasPastReservations.value = true

                                        val idStruct =
                                            it.data?.get("idstruttura") as DocumentReference

                                        val idStructStr =
                                            idStruct.path.substring(idStruct.path.lastIndexOf('/') + 1)

                                        reservationData["idstruttura"] = idStructStr
                                        reservationData["tiposport"] = it.data?.get("tiposport")
                                        reservationData["data"] = it.data?.get("data")
                                        reservationData["reservationid"] = it.id

                                        idStruct
                                            .get()
                                            .addOnSuccessListener {
                                                reservationData["nomestruttura"] =
                                                    it.data?.get("nomestruttura")
                                                reservationData["citta"] = it.data?.get("citta")

                                                idStruct
                                                    .collection("reviewStruttura")
                                                    .document(user.uid)
                                                    .get()
                                                    .addOnSuccessListener {
                                                        if (it.data?.get("voto1") != null) {
                                                            val voto1 =
                                                                (it.data?.get("voto1") as Long).toFloat()
                                                            val voto2 =
                                                                (it.data?.get("voto2") as Long).toFloat()
                                                            val voto3 =
                                                                (it.data?.get("voto3") as Long).toFloat()
                                                            val voto4 =
                                                                (it.data?.get("voto4") as Long).toFloat()

                                                            val avg =
                                                                (voto1 + voto2 + voto3 + voto4) / 4

                                                            reservationData["avg"] = avg
                                                        }

                                                        allUserReservations.add(reservationData)
                                                        _userReservations.value =
                                                            allUserReservations
                                                    }
                                                    .addOnFailureListener {
                                                        allUserReservations.add(reservationData)
                                                        _userReservations.value =
                                                            allUserReservations
                                                    }
                                            }
                                            .addOnFailureListener {
                                                allUserReservations.add(reservationData)
                                                _userReservations.value = allUserReservations
                                            }
                                    }
                                }
                            }
                    }
                }
            }
    }

    fun insertReservation(posresid : String, idstruttura : Any?, data: Date, idcampo: Any?, tiposport: String) {
        if (user != null && user.uid != null) {
            val reservationsRef = db.collection("reservations")
            val data = hashMapOf(
                "posresid" to posresid.trim(),
                "idstruttura" to idstruttura,
                "data" to data,
                "idcampo" to idcampo,
                "tiposport" to tiposport,
                "activeflag" to true,
            )

            reservationsRef
                .whereEqualTo("posresid", posresid.trim())
                .get()
                .addOnSuccessListener {listPosRes ->
                    if (listPosRes.isEmpty) {
                        reservationsRef
                            .document(posresid.trim())
                            .set(data)
                    }
                }
        }
    }

    fun insertReservationInUser(posresid : String, note: String) {
        if (user != null && user.uid != null) {
            val reservationsRef = db.collection("users").document(user.uid!!).collection("reservations")
            val data = hashMapOf(
                "posresid" to posresid.trim(),
                "note" to note,
            )

            reservationsRef
                .document(posresid.trim())
                .set(data)
        }
    }

    fun updateNoteReservation(idReservation: String, notes: String) {
        if (user != null && user.uid != null) {
            db
                .collection("users")
                .document(user.uid)
                .collection("reservations")
                .document(idReservation)
                .update("note", notes)
        }
    }

    fun deleteReservation(idreservation: String) {
        if (user != null && user.uid != null) {
            db
                .collection("posres")
                .document(idreservation)
                .get()
                .addOnSuccessListener {
                    val newPlayers: MutableList<DocumentReference> = mutableListOf()
                    val updates: MutableMap<String, Any> = mutableMapOf()
                    updates["flagattivo"] = true
                    if (it.data?.get("players") != null) {
                        val players = it.data?.get("players") as List<DocumentReference>
                        val userPath = db.document("users/${user?.uid}")
                        if (players.isNotEmpty()) {
                            for (player in players) {
                                if (player != userPath) {
                                    newPlayers.add(player)
                                }
                            }
                        }
                        if (newPlayers.isEmpty()) {
                            updates["players"] = FieldValue.delete()
                            db
                                .collection("reservations")
                                .document(idreservation.trim())
                                .delete()
                        } else {
                            updates["players"] = newPlayers
                        }
                    }
                    db
                        .collection("posres")
                        .document(idreservation)
                        .update(updates)
                    db
                        .collection("users")
                        .document(user.uid!!)
                        .collection("reservations")
                        .document(idreservation.trim())
                        .delete()
                }

        }
    }

    fun getSingleReservation(idreservation: String) {
        if (user != null && user.uid != null) {
            val reservationsRef = db.collection("reservations")

            reservationsRef
                .document(idreservation)
                .get()
                .addOnSuccessListener { listPosRes ->

                    var reservationData : MutableMap<String, Any?> = mutableMapOf()

                    if (listPosRes.data?.get("idstruttura") != null) {
                        val idStruct = listPosRes.data?.get("idstruttura") as DocumentReference

                        reservationData["reservationid"] = idreservation
                        reservationData["posresid"] = listPosRes.data?.get("posresid")
                        reservationData["data"] = listPosRes.data?.get("data")
                        reservationData["tiposport"] = listPosRes.data?.get("tiposport")
                        reservationData["idstruttura"] = idStruct

                        db
                            .collection("users")
                            .document(user.uid!!)
                            .collection("reservations")
                            .whereEqualTo("posresid", listPosRes.data?.get("posresid"))
                            .get()
                            .addOnSuccessListener { listReservation ->
                                for (reserv in listReservation) {
                                    reservationData["note"] = reserv["note"]

                                    db
                                        .collection("posres")
                                        .document(listPosRes.data?.get("posresid").toString())
                                        .get()
                                        .addOnSuccessListener {
                                            reservationData["flagattivo"] =
                                                it.data?.get("flagattivo")
                                            idStruct
                                                .get()
                                                .addOnSuccessListener {
                                                    reservationData["nomestruttura"] =
                                                        it.data?.get("nomestruttura")
                                                    _singleReservation.value = reservationData
                                                }
                                                .addOnFailureListener {
                                                    _singleReservation.value = reservationData
                                                }
                                        }
                                }
                            }
                    }

                }
        }
    }

    fun changeReservation(
        oldReservation: String,
        newReseservationId: String,
        idstruttura: Any?,
        data: Date,
        idcampo: Any?,
        tiposport: String,
        note: String
    ) {
        deleteReservation(oldReservation)
        insertReservation(newReseservationId, idstruttura, data, idcampo, tiposport)
        insertReservationInUser(newReseservationId, note)
    }
}