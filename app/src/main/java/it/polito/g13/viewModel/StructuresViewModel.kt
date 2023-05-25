package it.polito.g13.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StructuresViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _structures = MutableLiveData<List<MutableMap<String, Any>>>()
    val structures: LiveData<List<MutableMap<String, Any>>> = _structures

    private val _courts = MutableLiveData<List<MutableMap<String, Any>>>()
    val courts: LiveData<List<MutableMap<String, Any>>> = _courts

    private val _reviews = MutableLiveData<List<MutableMap<String, Any>>>()
    val reviews: LiveData<List<MutableMap<String, Any>>> = _reviews

    private lateinit var l: ListenerRegistration

    init {
        val structs = db.collection("struttura")

        structs
            .get()
            .addOnSuccessListener { listStructs ->
                val allStructs: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (struct in listStructs) {
                    val structData = struct.data
                    allStructs.add(structData)
                }

                _structures.value = allStructs

                val courtReferences = listStructs.map { struct ->
                    structs.document(struct.id)
                        .collection("campistruttura")
                }

                /*
                val reviewReferences = listStructs.map { struct ->
                    structs.document(struct.id)
                        .collection("reviewStruttura")
                }

                val meanRating = calculateMeanRating(reviewReferences)

                for (struct in listStructs) {
                    struct.data["meanRating"] = meanRating
                }

                 */

                fetchCourts(courtReferences, allStructs)
            }
    }

    private fun fetchCourts(courtReferences: List<CollectionReference>, allStructs: List<MutableMap<String, Any>>) {
        val allCourts: MutableList<MutableMap<String, Any>> = mutableListOf()

        val courtListeners = mutableListOf<ListenerRegistration>()

        courtReferences.forEachIndexed { index, courtReference ->

            val listener = courtReference
                .addSnapshotListener { value, error ->

                    if (error != null) {
                        _courts.value = emptyList()
                    }
                    else {
                        val courtsData = value?.mapNotNull { it.data }

                        courtsData?.let {
                            val structName = allStructs[index]["nomestruttura"] as? String
                            //val meanRating = allStructs[index]["meanRating"] as? String

                            it.forEach { court ->
                                court["nomestruttura"] = structName
                                //court["meanRating"] = meanRating
                            }

                            allCourts.addAll(it)
                        }

                        if (allCourts.size == courtReferences.size) {
                            _courts.value = allCourts

                            courtListeners.forEach { listener ->
                                listener.remove()
                            }
                        }
                    }
                }
            courtListeners.add(listener)
        }
    }

    private fun calculateMeanRating(ratingsReferences: List<CollectionReference>?): Double {
        var totalRating = 0.0
        var count = 0

        ratingsReferences?.forEachIndexed { index, collectionReference ->
            collectionReference.get().addOnSuccessListener {
                for (document in it) {
                    val rating = document.getDouble("voto1")
                        ?.plus(document.getDouble("voto2")!!)
                        ?.plus(document.getDouble("voto3")!!)
                        ?.plus(document.getDouble("voto4")!!)

                    if (rating != null) {
                        totalRating += rating
                        count += 4
                    }
                }
            }
        }

        return if (count > 0) totalRating / count else 0.0
    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }
}