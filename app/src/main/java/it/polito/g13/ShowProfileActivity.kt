package it.polito.g13

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences

class ShowProfileActivity : AppCompatActivity() {
    val sharedPreference:SharedPreferences =  getSharedPreferences("preferences", MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
    }
}