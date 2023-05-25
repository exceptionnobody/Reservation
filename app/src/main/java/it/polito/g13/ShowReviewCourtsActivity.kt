package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
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
import it.polito.g13.viewModel.ReservationsViewModel
import it.polito.g13.viewModel.ReviewStructureViewModel

@AndroidEntryPoint
class ShowReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reviewStructureViewModel by viewModels<ReviewStructureViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var selectedCourtName: String
    private var selectedCourtId: Int = 0
    private var selectedReviewId: Int = 0
    private var userId: Int = 1

    private lateinit var structureAndCourtsRatingBar: RatingBar
    private lateinit var equipmentRatingBar: RatingBar
    private lateinit var dressingRoomsRatingBar: RatingBar
    private lateinit var staffRatingBar: RatingBar

    private lateinit var feedbackRate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_review_courts)

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
        selectedCourtName = intent.getStringExtra("selectedCourtName").toString()
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Review $selectedCourtName"

        selectedCourtId = intent.getIntExtra("selectedCourtId", 0)

        // set value from db
        structureAndCourtsRatingBar = findViewById(R.id.rating_structure_courts)

        equipmentRatingBar = findViewById(R.id.rating_equipment)

        dressingRoomsRatingBar = findViewById(R.id.rating_dressing_rooms)

        staffRatingBar = findViewById(R.id.rating_staff)

        feedbackRate = findViewById(R.id.feedback_rate)

        reviewStructureViewModel.getReviewByStructureAndUserId(selectedCourtId,userId)

        reviewStructureViewModel.singleReviewStructure.observe(this) {
            if (it !== null) {
                selectedReviewId = it.id
                structureAndCourtsRatingBar.rating = it.s_q1.toFloat()
                equipmentRatingBar.rating = it.s_q2.toFloat()
                dressingRoomsRatingBar.rating = it.s_q3.toFloat()
                staffRatingBar.rating = it.s_q4.toFloat()
                if (it.description.isNotEmpty()) {
                    feedbackRate.text = it.description
                }
            }
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
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if (id==R.id.action_edit) {
            val intent = Intent(this, EditReviewCourtsActivity::class.java)
            intent.putExtra("selectedCourtName", selectedCourtName)
            intent.putExtra("selectedCourtId", selectedCourtId)
            intent.putExtra("selectedReviewId", selectedReviewId)
            intent.putExtra("userId", userId)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}