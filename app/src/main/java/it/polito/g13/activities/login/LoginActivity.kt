package it.polito.g13.activities.login


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.g13.R
import it.polito.g13.ReservationActivity
import it.polito.g13.activities.editprofile.EditProfileActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_CANCELED) {
                    finish()
                }

                if (result.resultCode == Activity.RESULT_OK) {

                    val data: Intent? = result.data
                    val response = IdpResponse.fromResultIntent(data)
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user != null) {
                        if (!user.isEmailVerified) {
                            userIsNotValidate(user, response)
                        } else {
                            Log.d("AUTENTICAZIONE", "SUCCESSO: " + response.toString())

                            if (response?.providerType.equals("google.com")) {
                                checkGoogleAccount(user)
                            } else {
                                checkNonGoogleAccount(user)
                            }
                        }

                    } else {
                        launchSignInFlow()
                    }

                }


            }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {

            if (currentUser.isEmailVerified) {
                val intent = Intent(this, ReservationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
            }


        } else {

            launchSignInFlow()
        }


    }

    private fun launchSignInFlow() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        val authUiLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_login)
            .setGoogleButtonId(R.id.GoogleButton)
            .setEmailButtonId(R.id.emailButton)
            .build()

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setAuthMethodPickerLayout(authUiLayout)
            .setLogo(R.drawable.logo_no_bg) // Set logo drawable
            .setTheme(R.style.Theme_Mad)
            .build()

        loginLauncher.launch(intent)
    }

    private fun checkNonGoogleAccount(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()

        val documentRef = db.collection("EmailVerification").document(user.email.toString())
        documentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot.exists()) {
//                        Log.d("AUTENTICAZIONE", "UTENTE GIA' VERIFICATO MA ANCORA NON REGISTRATO")
                        documentRef.delete()
                        val userRef = db.collection("users").document(user.email.toString())
                            .collection("infos").document(user.displayName.toString())
                        userRef.delete()
                        val intent = Intent(this, EditProfileActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
//                        Log.d("AUTENTICAZIONE","UTENTE LOGGATO CON ACCOUNT DIVERSA DA GOOGLE VERIFICATO")

                        val userRef = db.collection("users")
                            .document(user.uid)
                            .collection("loginInfo")
                            .document(user.displayName.toString())

                        val userInformations = hashMapOf(
                            "name" to user.displayName.toString(),
                            "email" to user.email,
                            "timestamp_registrazione" to FieldValue.serverTimestamp()
                        )
                        userRef.set(userInformations)
                            .addOnCompleteListener {
                                val intent = Intent(
                                    this,
                                    ReservationActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }

                    }
                } else {
                    // Errore durante il recupero del documento
                }
            }
            .addOnFailureListener { e ->
                // Errore da gestire
            }
    }

    private fun checkGoogleAccount(user: FirebaseUser) {

        val userRef = db.collection("users/${user.uid}/profile").document("info")


        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val documentSnapshot = task.result

                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    val jsonObject = JSONObject()


//                    Log.d("AUTENTICAZIONE", "UTENTE GIA' LOGGATO")
                    val sharedPreference =  getSharedPreferences("preferences", 0)
                    val profile = sharedPreference.getString("profile", "").toString()

                    if(profile!= ""){
                        Log.d("SHAREDPREFERENCES", "piene")
                    }else{
                        Log.d("SHAREDPREFERENCES", "vuote")

                        if(data != null) {
                            val myshare = sharedPreference.edit()
                            myshare.putString("user_name", data["name_surname"].toString())
                            jsonObject.put(
                                getString(R.string.save_username),
                                data["name_surname"].toString()
                            )
                            myshare.putString("user_nickname", data["nickname"].toString())
                            jsonObject.put(
                                getString(R.string.save_nickname),
                                data["nickname"].toString()
                            )
                            if(data["city"].toString() != "") {
                                myshare.putString("user_city", data["city"].toString())
                                jsonObject.put(
                                    getString(R.string.save_city),
                                    data["city"].toString()
                                )
                            }
                            if(data["gender"].toString() != "null") {
                                myshare.putString("user_gender", data["gender"] as String?)
                                jsonObject.put(
                                    getString(R.string.save_gender),
                                    data["gender"].toString()
                                )
                            }

                            if(data["phone_number"].toString() != "") {
                                myshare.putString("user_number", data["phone_number"].toString())
                                jsonObject.put(
                                    getString(R.string.save_telnumber),
                                    data["phone_number"].toString()
                                )
                            }
                            if(data["age"].toString() != "") {
                                myshare.putString("user_age", data["age"].toString())
                                jsonObject.put(
                                    getString(R.string.save_age),
                                    data["age"].toString()
                                )
                            }

                            if(data["description"].toString() != "") {
                                myshare.putString("user_description", data["description"].toString())
                                jsonObject.put(
                                    getString(R.string.save_description),
                                    data["description"].toString()
                                )
                            }

                            if(data["languages"].toString() != "") {
                                myshare.putString("user_languages", data["languages"].toString())
                                jsonObject.put(
                                    getString(R.string.save_languages),
                                    data["languages"].toString()
                                )
                            }

                            myshare.putString("profile", jsonObject.toString())
                            Log.d(
                                "SHAREDPREFERENCES",
                                "profile_lato_login: ${data}"
                            )
                            myshare.apply()
                        }
                    }
                    val intent = Intent(this, ReservationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
//                    Log.d("AUTENTICAZIONE", "UTENTE LOGGATO CON ACCOUNT GOOGLE MA NON ANCORA REGISTRATO")
                    val myuserRef = db.collection("users")
                        .document(user.uid)
                        .collection("loginInfo")
                        .document(user.displayName.toString())

                    val userInformations = hashMapOf(
                        "name" to user.displayName.toString(),
                        "email" to user.email,
                        "timestamp_registrazione" to FieldValue.serverTimestamp()
                    )
                    myuserRef.set(userInformations)
                        .addOnCompleteListener {
                            val intent = Intent(
                                this,
                                RegistrationActivity::class.java
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                }
            } else {
                // Errore durante il recupero del documento
            }
        }
    }

    private fun userIsNotValidate(user: FirebaseUser, response: IdpResponse?) {
        val userId = user.uid
        val email = user.email
        val documentRef = db.collection("EmailVerification").document(email!!)
        val userRef = db.collection("users").document(email)
            .collection("infos").document(user.displayName!!)

        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val intent = Intent(this, VerificationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (response?.providerType == "password") {
                        user.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    val emailVerification = hashMapOf(
                                        "userId" to userId,
                                        "timestamp" to FieldValue.serverTimestamp()
                                    )

                                    val userInformations = hashMapOf(
                                        "name" to user.displayName.toString(),
                                        "email" to email

                                    )

                                    db.collection("EmailVerification")
                                        .document(email)
                                        .set(emailVerification)
                                        .addOnSuccessListener {
                                            userRef.set(userInformations)
                                            val intent = Intent(
                                                this,
                                                ConfermationActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            // Errore durante la creazione
                                        }

                                } else {
                                    // Da gestire
                                    val _exception = task.exception

                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Errore durante il recupero del documento
            }


    }

}