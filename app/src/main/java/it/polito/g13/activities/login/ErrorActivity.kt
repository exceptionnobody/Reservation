package it.polito.g13.activities.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class ErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser

        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("It seems that there has been an error, please try again later.")
            .setPositiveButton("Ok") { dialog, which ->

                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

            }


        val dialog = dialogBuilder.create()
        dialog.show()
    }
}