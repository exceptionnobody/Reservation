package it.polito.g13

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.viewModel.PosResViewModel
import it.polito.g13.viewModel.ReservationsViewModel
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

    private val posResViewModel by viewModels<PosResViewModel>()
    private val reservationViewModel by viewModels<ReservationsViewModel>()

    private var selectedPosResId: Int = 0
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
        menuItemBrowseCourts.setActionView(R.layout.menu_item_review_courts)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservation detail"

        confirmButton = findViewById(R.id.confirm_button_posres)
        val cancelButton = findViewById<Button>(R.id.cancel_button_posres)

        notesInput = findViewById(R.id.content_notes_posres)

        //get selected reservation
        selectedPosResId = intent.getIntExtra("selectedPosResId", 0)
        posResViewModel.getPosResById(selectedPosResId)
        posResViewModel.singlePosRes.observe(this) {
            val sportText = findViewById<TextView>(R.id.content_sport_typology_posres)
            sportText.text = it.sport

            val placeText = findViewById<TextView>(R.id.content_place_posres)
            placeText.text = it.strut

            val dateTimeText = findViewById<TextView>(R.id.content_date_time_posres)
            val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.data).split(" ")
            val date = formattedDate[0]
            val hour1 = formattedDate[1]
            val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

            dateTimeText.text = date + ", " + hour1 + "-" + hour2
        }

        confirmButton.setOnClickListener {
            posResViewModel.singlePosRes.observe(this) {
                if (it != null) {
                    reservationViewModel.insertReservation(Reservation(99, it.id, 1, it.strut, it.sport, it.data, notesInput.text.toString(), true))
                    posResViewModel.updatePosRes(PosRes(it.id, it.strut, it.campo, it.sport, it.data, false))

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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}