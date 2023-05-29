package it.polito.g13.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.g13.entities.PosRes
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class PosResDBViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    private val _listPosRes = MutableLiveData<List<MutableMap<String, Any>>>()
    val listPosRes: LiveData<List<MutableMap<String, Any>>> = _listPosRes

    private val _singlePosRes = MutableLiveData<MutableMap<String, Any?>>()
    val singlePosRes: LiveData<MutableMap<String, Any?>> = _singlePosRes

    /*init {
        val posResCollection = db.collection("posres")

        posResCollection
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data
                    allPosRes.add(posResData)
                }

                _listPosRes.value = allPosRes

            }
    }*/

    public fun updatePosRes(posres: MutableMap<String, Any?>) {
        val posResCollection = db.collection("posres")
        val posresId = posres["posresid"].toString()
        val numberOfCurrentPlayers = posres["numberOfCurrentPlayers"].toString().toInt()
        val maxPeople = posres["maxpeople"].toString().toInt()
        val players : MutableList<DocumentReference> = mutableListOf()

        if (user != null && user.uid != null) {
            if (posres["players"] != null) {
                val playersPosres = posres["players"] as MutableList<DocumentReference>

                for (player in playersPosres) {
                    players.add(player)
                }
            }
            val userPath = db.document("users/${user?.uid}")

            players.add(userPath)

            if (numberOfCurrentPlayers + 1 == maxPeople) {
                posResCollection
                    .document(posresId.trim())
                    .update("flagattivo", false, "players", players)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            } else {
                posResCollection
                    .document(posresId.trim())
                    .update("players", players)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }
        }
    }

    public  fun getPostResBySportTimeCity(sport: String, from: String, to: String, city: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .whereEqualTo("tiposport", sport.lowercase())
            //.whereGreaterThanOrEqualTo("data", from)
            //.whereLessThanOrEqualTo("data", to)
            .whereEqualTo("citta", city.lowercase().trim())
            .whereEqualTo("flagattivo", true)
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data
                    var skipPosRes = false

                    posResData["posresid"] = posres_.id

                    if (posResData["players"] != null) {
                        val players = posResData["players"] as List<DocumentReference>

                        val userPath = db.document("users/${user?.uid}")

                        if (players.any{ it == userPath }) {
                            skipPosRes = true
                        }

                        posResData["numberOfCurrentPlayers"] = players.size.toString()
                    } else {
                        posResData["numberOfCurrentPlayers"] = "0"
                    }

                    val posResFrom = posResData["data"] as Timestamp
                    val seconds = posResFrom.seconds
                    val nanoseconds = posResFrom.nanoseconds
                    val milliseconds = seconds * 1000 + nanoseconds / 1_000_000

                    val date = SimpleDateFormat("HH:mm").format(Date(milliseconds))
                    val formattedDate = SimpleDateFormat("HH:mm").parse(date)

                    val formattedFrom = SimpleDateFormat("HH:mm").parse(from)
                    val formattedTo = SimpleDateFormat("HH:mm").parse(to)

                    if ((formattedDate.equals(formattedFrom) || formattedDate.equals(formattedTo) ||
                        (formattedDate.after(formattedFrom) && formattedDate.before(formattedTo))) && !skipPosRes) {

                        val idStruct = posResData["idstruttura"] as DocumentReference

                        idStruct
                            .get()
                            .addOnSuccessListener {
                                posResData["nomestruttura"] = it.data?.get("nomestruttura")
                            }

                        allPosRes.add(posResData)
                    }
                }

                _listPosRes.value = allPosRes

            }
    }

    public  fun getPosResById(posresId: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .document(posresId)
            .get()
            .addOnSuccessListener { listPosRes ->
                val posresInfo : MutableMap<String, Any?> = mutableMapOf()

                posresInfo.put("citta", listPosRes.get("citta"))
                posresInfo.put("data", listPosRes.get("data"))
                posresInfo.put("idcampo", listPosRes.get("idcampo"))
                posresInfo.put("idstruttura", listPosRes.get("idstruttura"))
                posresInfo.put("maxpeople", listPosRes.get("maxpeople"))
                posresInfo.put("players", listPosRes.get("players"))
                posresInfo.put("tiposport", listPosRes.get("tiposport"))
                posresInfo.put("posresid", listPosRes.id)

                if (listPosRes.get("players") != null) {
                    val players = listPosRes.get("players") as List<DocumentReference>

                    posresInfo.put("numberOfCurrentPlayers", players.size.toString())
                } else {
                    posresInfo.put("numberOfCurrentPlayers", "0")
                }

                val idStruct = listPosRes.get("idstruttura") as DocumentReference

                idStruct
                    .get()
                    .addOnSuccessListener {
                        posresInfo.put("nomestruttura", it.data?.get("nomestruttura"))
                        _singlePosRes.value = posresInfo
                    }
                    .addOnFailureListener {
                        _singlePosRes.value = posresInfo
                    }

            }
    }

    public  fun getPosResByStructureSportDateAndTime(sport: String, date: Date, from: String, to: String, struct: DocumentReference, currentPosResId: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .whereEqualTo("idstruttura", struct)
            .whereEqualTo("tiposport", sport)
            //.whereGreaterThanOrEqualTo("data", from)
            //.whereLessThanOrEqualTo("data", to)
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data
                    var skipPosRes = false

                    posResData["posresid"] = posres_.id

                    if (posResData["players"] != null) {
                        val players = posResData["players"] as List<DocumentReference>

                        val userPath = db.document("users/${user?.uid}")

                        if (players.any{ it == userPath } && posResData["posresid"].toString() != currentPosResId) {
                            skipPosRes = true
                        }

                        posResData["numberOfCurrentPlayers"] = players.size.toString()
                    } else {
                        posResData["numberOfCurrentPlayers"] = "0"
                    }

                    if (posResData["posresid"].toString() != currentPosResId && posResData["flagattivo"] == false) {
                        skipPosRes = true
                    }

                    val posResFrom = posResData["data"] as Timestamp
                    val seconds = posResFrom.seconds
                    val nanoseconds = posResFrom.nanoseconds
                    val milliseconds = seconds * 1000 + nanoseconds / 1_000_000

                    val datePosRes = SimpleDateFormat("HH:mm").format(Date(milliseconds))
                    val formatteTime = SimpleDateFormat("HH:mm").parse(datePosRes)
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(Date(milliseconds))
                    val formattedDateSelected = SimpleDateFormat("yyyy-MM-dd").format(date)

                    val formattedFrom = SimpleDateFormat("HH:mm").parse(from)
                    val formattedTo = SimpleDateFormat("HH:mm").parse(to)

                    if (
                        (
                            formatteTime.equals(formattedFrom) || formatteTime.equals(formattedTo) ||
                            (formatteTime.after(formattedFrom) && formatteTime.before(formattedTo))
                        )
                        && formattedDateSelected == formattedDate
                        && !skipPosRes
                    ) {
                        val idStruct = posResData["idstruttura"] as DocumentReference

                        idStruct
                            .get()
                            .addOnSuccessListener {
                                posResData["nomestruttura"] = it.data?.get("nomestruttura")

                                allPosRes.add(posResData)
                                _listPosRes.value = allPosRes
                            }
                    }
                }
            }
    }
}