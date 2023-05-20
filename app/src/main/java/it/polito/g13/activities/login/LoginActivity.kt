package it.polito.g13.activities.login


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import it.polito.g13.R
import it.polito.g13.ReservationActivity
import java.util.Arrays

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1822

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica se l'utente è già autenticato
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, ReservationActivity::class.java)
            startActivity(intent)

        } else {
            Log.d("AUTENTICAZIONE", "UTENTE NON IDENTIFICATO")

            launchSignInFlow()
        }
    }

    private fun launchSignInFlow() {
        val providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            // Aggiungi altri provider se necessario
        )

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.my_great_logo) // Set logo drawable
            .build()

        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            Log.d("AUTENTICAZIONE", "${response.toString()}")

            if (resultCode == Activity.RESULT_OK) {
                Log.d("AUTENTICAZIONE", "SUCCESSO")
                val intent = Intent(this, ReservationActivity::class.java)
                startActivity(intent)

            } else {
                Log.d("AUTENTICAZIONE", "FALLIMENTO")
            }
        }
    }
}