package it.polito.g13.activities.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.displayName

        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Sembra che ancora non sia stata confermata la tua email.\nSe non hai ricevuto l'email o risconti dei problemi, contattaci!")
            .setPositiveButton("Riprova", DialogInterface.OnClickListener { dialog, which ->

                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

            })
            .setNegativeButton("Contattaci") { dialog, which ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}