package it.polito.g13.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewsDBViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _structures = MutableLiveData<List<MutableMap<String, Any>>>()
    val structures: LiveData<List<MutableMap<String, Any>>> = _structures

    private val _reviews = MutableLiveData<List<MutableMap<String, Any>>>()
    val reviews: LiveData<List<MutableMap<String, Any>>> = _reviews

    private val _structReviews = MutableLiveData<List<MutableMap<String, Any>>>()
    val structReviews: LiveData<List<MutableMap<String, Any>>> = _structReviews

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

                val reviewReferences = listStructs.map { struct ->
                    structs.document(struct.id)
                        .collection("reviewStruttura")
                }

                fetchReviews(reviewReferences, allStructs)
            }
    }

    private fun fetchReviews(reviewReferences: List<CollectionReference>, allStructs: List<MutableMap<String, Any>>) {
        val allReviews: MutableList<MutableMap<String, Any>> = mutableListOf()

        val reviewListeners = mutableListOf<ListenerRegistration>()

        reviewReferences.forEachIndexed { index, courtReference ->

            val listener = courtReference
                .addSnapshotListener { value, error ->

                    if (error != null) {
                        _reviews.value = emptyList()
                    }
                    else {
                        val reviewsData = value?.mapNotNull { it.data }

                        reviewsData?.let {
                            val idStruct = allStructs[index]["idstruttura"] as? String

                            it.forEach { review ->
                                review["idstruttura"] = idStruct
                            }

                            allReviews.addAll(it)
                        }

                        if (allReviews.size == reviewReferences.size) {
                            _reviews.value = allReviews

                            reviewListeners.forEach { listener ->
                                listener.remove()
                            }
                        }
                    }
                }
            reviewListeners.add(listener)
        }
    }

    fun getReviewsByStruct(idStruct: String) {

        db
            .collection("struttura")
            .document(idStruct)
            .collection("reviewStruttura")
            .get()
            .addOnSuccessListener { listReviews ->
                val allStructReviews: MutableList<MutableMap<String, Any>> = mutableListOf()

                for (review in listReviews) {
                    allStructReviews.add(review.data)
                }

                _structReviews.value = allStructReviews
            }
    }

    override fun onCleared() {
        super.onCleared()
        l.remove()
    }
}