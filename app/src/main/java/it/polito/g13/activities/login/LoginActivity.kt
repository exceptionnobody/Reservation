package it.polito.g13.activities.login


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.g13.R
import it.polito.g13.activities.editprofile.ShowProfileActivity
import java.util.Arrays

class LoginActivity : AppCompatActivity() {

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_CANCELED) {
                finish()
            }

            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val response = IdpResponse.fromResultIntent(data)
                val user = FirebaseAuth.getInstance().currentUser
                val db = FirebaseFirestore.getInstance()

                if (user != null) {
                        val email = user.email
                        val userId = user.uid
                        if (!user.isEmailVerified) {

                            val documentRef =  db.collection("EmailVerification").document(user.email.toString())
                            val userRef =  db.collection("users").document(user.email.toString()).collection("infos").document(user.displayName.toString())

                            documentRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                                val intent = Intent(this, VerificationActivity::class.java)
                                                startActivity(intent)
                                                finish()

                                    } else {
                                        if (email != null && response?.providerType == "password") {
                                            user.sendEmailVerification()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                        val documentName = user.email.toString()

                                                        val emailVerification = hashMapOf(
                                                            "userId" to userId,
                                                            "timestamp" to FieldValue.serverTimestamp()
                                                        )

                                                        val userInformations = hashMapOf(
                                                            "name" to user.displayName.toString(),
                                                            "email" to user.email

                                                        )

                                                        db.collection("EmailVerification")
                                                            .document(documentName)
                                                            .set(emailVerification)
                                                            .addOnSuccessListener {
                                                                userRef.set(userInformations)
                                                                val intent = Intent(this, ConfermationActivity::class.java)
                                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                            .addOnFailureListener { e ->
                                                                // Errore durante la creazione
                                                            }

                                                    } else {
                                                        // Da gestire
                                                        val exception = task.exception

                                                    }
                                                }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Errore durante il recupero del documento
                                }


                        } else {
                            Log.d("AUTENTICAZIONE", "SUCCESSO: " + response.toString())
                            val documentRef =   db.collection("EmailVerification").document(user.email.toString())

                            documentRef.delete()
                                .addOnSuccessListener {
                                    val intent = Intent(this, ShowProfileActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Errore da gestire
                                }

                        }

                } else {
                    launchSignInFlow()
                }

            }


        }

        val  currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {

              if (currentUser.isEmailVerified) {
                  val intent = Intent(this, ShowProfileActivity::class.java)
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                  startActivity(intent)
                } else {
                    AuthUI.getInstance()
                      .signOut(this)
                        .addOnCompleteListener {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }



        } else {

            launchSignInFlow()
        }



    }

    private fun launchSignInFlow() {
        val providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo_no_bg) // Set logo drawable
            .setTheme(R.style.Theme_Mad)
            .build()

        loginLauncher.launch(intent)
    }


}