package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StructuresDBViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _structures = MutableLiveData<List<MutableMap<String, Any>>>()
    val structures: LiveData<List<MutableMap<String, Any>>> = _structures

    private val _courts = MutableLiveData<List<MutableMap<String, Any>>>()
    val courts: LiveData<List<MutableMap<String, Any>>> = _courts

    init {
        val structs = db.collection("struttura")

        structs
            .get()
            .addOnSuccessListener { listStructs ->
                val allStructs: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (struct in listStructs) {
                    val structData = struct.data

                    structs
                        .document(structData["idstruttura"] as String)
                        .collection("reviewStruttura")
                        .get()
                        .addOnSuccessListener { listReviews ->
                            var sum = 0.toFloat()
                            var count = 0

                            for (review in listReviews) {
                                val reviewData = review.data

                                sum += (reviewData["voto1"] as Long).toFloat() + (reviewData["voto2"] as Long).toFloat() +
                                        (reviewData["voto3"] as Long).toFloat() + (reviewData["voto4"] as Long).toFloat()

                                count += 4
                            }

                            val avg = sum / count
                            structData["avg"] = avg
                            allStructs.add(structData)
                            _structures.value = allStructs

                            val courtReferences = listStructs.map { struct ->
                                structs.document(struct.id)
                                    .collection("campistruttura")
                            }

                            fetchCourts(courtReferences, allStructs)
                        }
                        .addOnFailureListener {
                            allStructs.add(structData)
                            _structures.value = allStructs

                            val courtReferences = listStructs.map { struct ->
                                structs.document(struct.id)
                                    .collection("campistruttura")
                            }

                            fetchCourts(courtReferences, allStructs)
                        }
                }
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
                    else if (index < allStructs.size) {
                        val courtsData = value?.mapNotNull { it.data }

                        courtsData?.let {
                            val idStruct = allStructs[index]["idstruttura"] as? String
                            val structName = allStructs[index]["nomestruttura"] as? String
                            val structCity = allStructs[index]["citta"] as? String
                            val avg = allStructs[index]["avg"] as? Float

                            it.forEach { court ->
                                court["nomestruttura"] = structName
                                court["idstruttura"] = idStruct
                                court["citta"] = structCity
                                court["avg"] = avg
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
}