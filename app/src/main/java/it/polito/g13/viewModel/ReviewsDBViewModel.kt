package it.polito.g13.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class ReviewsDBViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    private val _structures = MutableLiveData<List<MutableMap<String, Any>>>()
    val structures: LiveData<List<MutableMap<String, Any>>> = _structures

    private val _reviews = MutableLiveData<List<MutableMap<String, Any>>>()
    val reviews: LiveData<List<MutableMap<String, Any>>> = _reviews

    private val _structReviews = MutableLiveData<List<MutableMap<String, Any>>>()
    val structReviews: LiveData<List<MutableMap<String, Any>>> = _structReviews

    private val _reviewById = MutableLiveData<MutableMap<String, Any>>()
    val reviewById: LiveData<MutableMap<String, Any>> = _reviewById

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

    fun insertReviewInUser(structId : String, voto1: Int, voto2: Int, voto3: Int, voto4: Int, comment: String) {
        if (user != null && user.uid != null) {
            val reviewsRef = db.collection("users").document(user.uid!!).collection("reviews")

            val data = hashMapOf(
                "struttura" to "/struttura/${structId.trim()}",
                "voto1" to voto1,
                "voto2" to voto2,
                "voto3" to voto3,
                "voto4" to voto4,
                "comment" to comment
            )

            reviewsRef
                .document(structId.trim())
                .set(data)
        }
    }

    fun insertReviewInStructure(structId : String, voto1: Int, voto2: Int, voto3: Int, voto4: Int, comment: String) {
        if (user != null && user.uid != null) {
            val reviewsRef =
                db.collection("struttura").document(structId).collection("reviewStruttura")

            val data = hashMapOf(
                "struttura" to "/struttura/${structId.trim()}",
                "voto1" to voto1,
                "voto2" to voto2,
                "voto3" to voto3,
                "voto4" to voto4,
                "comment" to comment
            )

            reviewsRef
                .document(user.uid.trim())
                .set(data)
        }
    }

    fun getReviewById(structId: String) {
        if (user != null && user.uid != null) {
            db
                .collection("users")
                .document(user.uid)
                .collection("reviews")
                .document(structId)
                .get()
                .addOnSuccessListener {
                    val reviewData: MutableMap<String, Any> = mutableMapOf()

                    reviewData["voto1"] = (it.data?.get("voto1") as Long).toInt()
                    reviewData["voto2"] = (it.data?.get("voto2") as Long).toInt()
                    reviewData["voto3"] = (it.data?.get("voto3") as Long).toInt()
                    reviewData["voto4"] = (it.data?.get("voto4") as Long).toInt()
                    reviewData["comment"] = it.data?.get("comment") as String

                    _reviewById.value = reviewData
                }
                .addOnFailureListener {
                    _reviewById.value = mutableMapOf()
                }
        }
    }

    fun updateReviewInUser(structId : String, voto1: Int, voto2: Int, voto3: Int, voto4: Int, comment: String) {
        if (user != null && user.uid != null) {

            db
                .collection("users")
                .document(user.uid)
                .collection("reviews")
                .document(structId)
                .update("voto1", voto1,
                    "voto2", voto2,
                    "voto3", voto3,
                    "voto4", voto4,
                    "comment", comment)
        }
    }

    fun updateReviewInStructure(structId : String, voto1: Int, voto2: Int, voto3: Int, voto4: Int, comment: String) {
        if (user != null && user.uid != null) {
            db
                .collection("struttura")
                .document(structId)
                .collection("reviewStruttura")
                .document(user.uid)
                .update("voto1", voto1,
                    "voto2", voto2,
                    "voto3", voto3,
                    "voto4", voto4,
                    "comment", comment)
        }
    }
}