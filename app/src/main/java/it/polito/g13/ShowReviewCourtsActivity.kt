package it.polito.g13

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
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
import it.polito.g13.viewModel.ReviewsDBViewModel

@AndroidEntryPoint
class ShowReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reviewStructureViewModel by viewModels<ReviewsDBViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var selectedCourtName: String
    private lateinit var selectedCourtId: String

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

        selectedCourtId = intent.getStringExtra("selectedCourtId").toString()

        // set value from db
        structureAndCourtsRatingBar = findViewById(R.id.rating_structure_courts)

        equipmentRatingBar = findViewById(R.id.rating_equipment)

        dressingRoomsRatingBar = findViewById(R.id.rating_dressing_rooms)

        staffRatingBar = findViewById(R.id.rating_staff)

        feedbackRate = findViewById(R.id.feedback_rate)

        reviewStructureViewModel.getReviewById(selectedCourtId)

        reviewStructureViewModel.reviewById.observe(this) {
            if (it.isNotEmpty()) {

                structureAndCourtsRatingBar.rating = (it["voto1"] as Int).toFloat()
                equipmentRatingBar.rating = (it["voto2"] as Int).toFloat()
                dressingRoomsRatingBar.rating = (it["voto3"] as Int).toFloat()
                staffRatingBar.rating = (it["voto4"] as Int).toFloat()

                if (it["comment"] !== "" && it["comment"] !== null) {
                    feedbackRate.setText(it["comment"] as String)
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
                        val sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.clear()
                        editor.apply()
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
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}