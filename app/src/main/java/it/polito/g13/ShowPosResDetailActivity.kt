package it.polito.g13

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.activities.login.LoginActivity
import it.polito.g13.viewModel.PosResDBViewModel
import it.polito.g13.viewModel.ReservationsDBViewModel
import java.text.SimpleDateFormat
import java.util.*

private var selectedSport: String? = null

@AndroidEntryPoint
class ShowPosResDetailActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var notesInput: EditText

    private val posResViewModel by viewModels<PosResDBViewModel>()
    private val reservationViewModel by viewModels<ReservationsDBViewModel>()

    private lateinit var selectedPosResId: String
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pos_res_detail)

        //get selected sport
        selectedSport = intent.getStringExtra("selectedSport")

        //toolbar instantiation
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val menuItemProfile = navView.menu.findItem(R.id.nav_profile)
        menuItemProfile.setActionView(R.layout.menu_item_profile)

        val menuItemReservations = navView.menu.findItem(R.id.nav_reservations)
        menuItemReservations.setActionView(R.layout.menu_item_reservations)

        val menuItemBookReservation = navView.menu.findItem(R.id.nav_book_reservation)
        menuItemBookReservation.setActionView(R.layout.menu_item_book_reservation)

        val menuItemReviewCourts = navView.menu.findItem(R.id.nav_review_courts)
        menuItemReviewCourts.setActionView(R.layout.menu_item_review_courts)

        val menuItemBrowseCourts = navView.menu.findItem(R.id.nav_browse_courts)
        menuItemBrowseCourts.setActionView(R.layout.menu_item_browse_courts)

        val menuItemExit = navView.menu.findItem(R.id.nav_exit)
        menuItemExit.setActionView(R.layout.menu_item_exit)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservation detail"

        confirmButton = findViewById(R.id.confirm_button_posres)
        val cancelButton = findViewById<Button>(R.id.cancel_button_posres)

        notesInput = findViewById(R.id.content_notes_posres)

        val loadingDetails = findViewById<ProgressBar>(R.id.loading_pos_res_detail)
        val container = findViewById<LinearLayout>(R.id.containerPosRes)

        //get selected reservation
        selectedPosResId = intent.getStringExtra("selectedPosResId").toString()
        posResViewModel.getPosResById(selectedPosResId)
        posResViewModel.singlePosRes.observe(this) {
            if (it !== null) {
                val sportText = findViewById<TextView>(R.id.content_sport_typology_posres)
                sportText.text = it["tiposport"].toString()

                val placeText = findViewById<TextView>(R.id.content_place_posres)
                if (it["nomestruttura"].toString() !== "null") {
                    placeText.text = it["nomestruttura"].toString()
                }

                val dateTimeText = findViewById<TextView>(R.id.content_date_time_posres)
                val timestamp = it["data"] as com.google.firebase.Timestamp
                val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                val netDate = Date(milliseconds)
                val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm").format(netDate).split(" ")
                val date = formattedDate[0]
                val hour1 = formattedDate[1]
                val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

                dateTimeText.text = date + ", " + hour1 + "-" + hour2

                loadingDetails.visibility = View.GONE
                container.visibility = View.VISIBLE
            }
        }

        confirmButton.setOnClickListener {
            posResViewModel.singlePosRes.observe(this) {
                if (it != null) {
                    val timestamp = it["data"] as com.google.firebase.Timestamp
                    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                    val netDate = Date(milliseconds)
                    reservationViewModel.insertReservationInUser(it["posresid"].toString(), notesInput.text.toString())
                    reservationViewModel.insertReservation(it["posresid"].toString(), it["idstruttura"], netDate, it["idcampo"], it["tiposport"].toString())
                    posResViewModel.updatePosRes(it)

                    val intent = Intent(this, ReservationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, BrowseAvailabilityActivity::class.java)
            startActivity(intent)
        }
    }

    //handle toolbar items
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ShowProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_reservations -> {
                val intent = Intent(this, ReservationActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_book_reservation -> {
                val intent = Intent(this, BrowseAvailabilityActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_review_courts -> {
                val intent = Intent(this, ListReviewCourtsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_browse_courts -> {
                val intent = Intent(this, BrowseCourtsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_exit -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // ...
                        val sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.clear()
                        editor.apply()
                        Log.d("SHAREDPREFERENCES", "cancello le shared preferences")
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}