package it.polito.g13.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
            .document(posresId)
            .update("flagattivo", flag)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    public  fun getPostResBySportTime(sport: String, from: String, to: String) {
        val posResCollection = db.collection("posres")

        posResCollection
            .whereEqualTo("tiposport", sport)
            .whereGreaterThanOrEqualTo("data", from)
            .whereLessThanOrEqualTo("data", to)
            .get()
            .addOnSuccessListener { listPosRes ->
                val allPosRes: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (posres_ in listPosRes) {
                    val posResData = posres_.data
                    allPosRes.add(posResData)
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
                _singlePosRes.value = listPosRes.data
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
                    allPosRes.add(posResData)
                }

                _listPosRes.value = allPosRes

            }
    }
}