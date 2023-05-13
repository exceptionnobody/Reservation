package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class EditReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var selectedCourtName: String

    private lateinit var structureAndCourtsRatingBar: RatingBar
    private lateinit var equipmentRatingBar: RatingBar
    private lateinit var dressingRoomsRatingBar: RatingBar
    private lateinit var staffRatingBar: RatingBar
    private lateinit var confirmButton: Button

    private var structureAndCourtsRatingValue: Int = 0
    private var equipmentRatingValue: Int = 0
    private var dressingRoomsRatingValue: Int = 0
    private var staffRatingValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review_courts)

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

        //set text navbar
        selectedCourtName = intent.getStringExtra("selectedCourtName").toString()
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Review $selectedCourtName"

        confirmButton = findViewById<Button>(R.id.confirm_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        cancelButton.setOnClickListener {
            val intent = Intent(this, ShowReviewCourtsActivity::class.java)
            startActivity(intent)
        }

        structureAndCourtsRatingBar = findViewById(R.id.rating_structure_courts)
        structureAndCourtsRatingBar.setOnRatingBarChangeListener( { ratingBar, rating, fromUser ->
            structureAndCourtsRatingValue = rating.toInt()
            checkConfirmButtonState()
        })

        equipmentRatingBar = findViewById(R.id.rating_equipment)
        equipmentRatingBar.setOnRatingBarChangeListener( { ratingBar, rating, fromUser ->
            equipmentRatingValue = rating.toInt()
            checkConfirmButtonState()
        })

        dressingRoomsRatingBar = findViewById(R.id.rating_dressing_rooms)
        dressingRoomsRatingBar.setOnRatingBarChangeListener( { ratingBar, rating, fromUser ->
            dressingRoomsRatingValue = rating.toInt()
            checkConfirmButtonState()
        })

        staffRatingBar = findViewById(R.id.rating_staff)
        staffRatingBar.setOnRatingBarChangeListener( { ratingBar, rating, fromUser ->
            staffRatingValue = rating.toInt()
            checkConfirmButtonState()
        })

        checkConfirmButtonState()
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun checkConfirmButtonState () {
        if (this::confirmButton.isInitialized) {
            if(
                structureAndCourtsRatingValue == 0 ||
                equipmentRatingValue == 0 ||
                dressingRoomsRatingValue == 0 ||
                staffRatingValue == 0
            ) {
                confirmButton.isClickable = false
                confirmButton.setBackgroundColor(Color.GRAY)
            } else {
                confirmButton.isClickable = true
                confirmButton.setBackgroundColor(resources.getColor(R.color.primary_green))
            }
        }
    }
}
