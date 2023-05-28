package it.polito.g13.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.g13.entities.PosRes
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class PosResDBViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _listPosRes = MutableLiveData<List<MutableMap<String, Any>>>()
    val listPosRes: LiveData<List<MutableMap<String, Any>>> = _listPosRes

    private val _singlePosRes = MutableLiveData<MutableMap<String, Any>>()
    val singlePosRes: LiveData<MutableMap<String, Any>> = _singlePosRes

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

    public fun updatePosRes(posresId: String, flag: Boolean) {
        val posResCollection = db.collection("posres")

        posResCollection
            .document(posresId.trim())
            .update("flagattivo", flag)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    public  fun getPostResBySportTimeCity(sport: String, from: String, to: String, city: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .whereEqualTo("tiposport", sport.lowercase())
            //.whereGreaterThanOrEqualTo("data", from)
            //.whereLessThanOrEqualTo("data", to)
            .whereEqualTo("citta", city.lowercase())
            .whereEqualTo("flagattivo", true)
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data

                    posResData["posresid"] = posres_.id

                    if (posResData["players"] != null) {
                        val players = posResData["players"] as List<DocumentReference>

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

                    if (formattedDate.equals(formattedFrom) || formattedDate.equals(formattedTo) ||
                        (formattedDate.after(formattedFrom) && formattedDate.before(formattedTo))) {

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
            .whereEqualTo("posresid", posresId)
            .get()
            .addOnSuccessListener { listPosRes ->

                for (posres_ in listPosRes) {
                    val posResData = posres_.data

                    posResData["posresid"] = posres_.id

                    val idStruct = posResData["idstruttura"] as DocumentReference

                    idStruct
                        .get()
                        .addOnSuccessListener {
                            posResData["nomestruttura"] = it.data?.get("nomestruttura")
                            _singlePosRes.value = posResData
                        }
                        .addOnFailureListener {
                            _singlePosRes.value = posResData
                        }
                }
            }
    }

    public  fun getPosResByStructureSportDateAndTime(sport: String, date: Date, from: String, to: String, struct: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .whereEqualTo("idstruttura", struct)
            .whereEqualTo("tiposport", sport)
            .whereGreaterThanOrEqualTo("data", from)
            .whereLessThanOrEqualTo("data", to)
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data
                    posResData["posresid"] = posres_.id
                    allPosRes.add(posResData)
                }

                _listPosRes.value = allPosRes

            }
    }
}