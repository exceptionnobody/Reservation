package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.entities.review_struct
import it.polito.g13.viewModel.ReviewStructureViewModel


@AndroidEntryPoint
class EditReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reviewStructureViewModel by viewModels<ReviewStructureViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var selectedCourtName: String
    private var reviewExist: Boolean = false
    private var selectedReviewId: Int = 0
    private var selectedCourtId: Int = 0
    private var userId: Int = 1

    private lateinit var structureAndCourtsRatingBar: RatingBar
    private lateinit var equipmentRatingBar: RatingBar
    private lateinit var dressingRoomsRatingBar: RatingBar
    private lateinit var staffRatingBar: RatingBar
    private lateinit var confirmButton: Button
    private lateinit var feedbackRate: EditText

    private var structureAndCourtsRatingValue: Int = 0
    private var equipmentRatingValue: Int = 0
    private var dressingRoomsRatingValue: Int = 0
    private var staffRatingValue: Int = 0

    private var initialStructureAndCourtsRatingValue: Int = 0
    private var initialEquipmentRatingValue: Int = 0
    private var initialDressingRoomsRatingValue: Int = 0
    private var initialStaffRatingValue: Int = 0
    private var initialFeedbackValue: String = ""

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

        val menuItemBrowseCourts = navView.menu.findItem(R.id.nav_browse_courts)
        menuItemBrowseCourts.setActionView(R.layout.menu_item_review_courts)

        //set text navbar
        selectedCourtName = intent.getStringExtra("selectedCourtName").toString()
        userId = intent.getIntExtra("userId", 0)
        selectedReviewId = intent.getIntExtra("selectedReviewId", 0)
        selectedCourtId = intent.getIntExtra("selectedCourtId", 0)
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Review $selectedCourtName"

        confirmButton = findViewById<Button>(R.id.confirm_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        confirmButton.setOnClickListener {
            if (reviewExist) {
                reviewStructureViewModel.updateReview(review_struct(
                    selectedReviewId, userId, selectedCourtId, 0, structureAndCourtsRatingValue, equipmentRatingValue, dressingRoomsRatingValue, staffRatingValue, 0, feedbackRate.text.toString()
                ))
            } else {
                reviewStructureViewModel.insertReview(review_struct(
                    0, userId, selectedCourtId, 0, structureAndCourtsRatingValue, equipmentRatingValue, dressingRoomsRatingValue, staffRatingValue, 0, feedbackRate.text.toString()
                ))
            }
            val intent = Intent(this, ShowReviewCourtsActivity::class.java)
            intent.putExtra("selectedCourtName", selectedCourtName)
            intent.putExtra("selectedCourtId", selectedCourtId)
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, ShowReviewCourtsActivity::class.java)
            intent.putExtra("selectedCourtName", selectedCourtName)
            intent.putExtra("selectedCourtId", selectedCourtId)
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

        feedbackRate = findViewById(R.id.feedback_rate)
        feedbackRate.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                checkConfirmButtonState()
            }

        })

        reviewStructureViewModel.getReviewById(selectedReviewId)

        reviewStructureViewModel.singleReviewStructure.observe(this) {
            if (it !== null) {
                reviewExist = true
                structureAndCourtsRatingBar.rating = it.s_q1.toFloat()
                initialStructureAndCourtsRatingValue = it.s_q1
                equipmentRatingBar.rating = it.s_q2.toFloat()
                initialEquipmentRatingValue = it.s_q2
                dressingRoomsRatingBar.rating = it.s_q3.toFloat()
                initialDressingRoomsRatingValue = it.s_q3
                staffRatingBar.rating = it.s_q4.toFloat()
                initialStaffRatingValue = it.s_q4
                if (it.description !== "" && it.description !== null) {
                    initialFeedbackValue = it.description
                    feedbackRate.setText(it.description)
                }
            }
        }

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
            R.id.nav_browse_courts -> {
                val intent = Intent(this, BrowseCourtsActivity::class.java)
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
                staffRatingValue == 0 ||
                (
                    structureAndCourtsRatingValue == initialStructureAndCourtsRatingValue &&
                    equipmentRatingValue == initialEquipmentRatingValue &&
                    dressingRoomsRatingValue == initialDressingRoomsRatingValue &&
                    staffRatingValue == initialStaffRatingValue &&
                    feedbackRate.text.toString() == initialFeedbackValue
                )
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
