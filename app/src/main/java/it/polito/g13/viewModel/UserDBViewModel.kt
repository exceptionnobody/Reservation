package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDBViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    private val _userData = MutableLiveData<MutableMap<String, Any>>()
    val userData: LiveData<MutableMap<String, Any>> = _userData

    init {
        if (user != null && user.uid != null) {

            db
                .collection("users")
                .document(user.uid)
                .collection("profile")
                .document("info")
                .get()
                .addOnSuccessListener {
                    _userData.value = it.data
                }
        }
    }
}