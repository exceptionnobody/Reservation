package it.polito.g13.activities.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class ConfermationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.displayName

        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Benvenuto $userId!\nTi è stata inviata una email di conferma. Ti aspettiamo!")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            })

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}