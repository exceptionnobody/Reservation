package it.polito.g13.activities.login


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import it.polito.g13.R
import it.polito.g13.ReservationActivity
import java.util.Arrays

class LoginActivity : AppCompatActivity() {

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val response = IdpResponse.fromResultIntent(data)

                Log.d("AUTENTICAZIONE", response.toString())

                Log.d("AUTENTICAZIONE", "SUCCESSO")
                val intent = Intent(this, ReservationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Log.d("AUTENTICAZIONE", "FALLIMENTO")
            }
        }

        // Verifica se l'utente è già autenticato
        val  currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, ReservationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

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
        )

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo_no_bg) // Set logo drawable
            .setTheme(R.style.Theme_Mad)
            .build()

        loginLauncher.launch(intent)
    }

}