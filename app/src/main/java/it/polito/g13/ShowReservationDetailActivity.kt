package it.polito.g13

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.viewModel.ReservationsViewModel
import java.text.SimpleDateFormat

@AndroidEntryPoint
class ShowReservationDetailActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private val reservationViewModel by viewModels<ReservationsViewModel> ()
    private var selectedReservationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_reservation_detail)

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

        val buttonOpenPopupDelete = findViewById<Button>(R.id.open_popup_delete)
        buttonOpenPopupDelete.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View) {
                    showPopupWindow(v)
                }
            }
        )

        //get selected reservation
        selectedReservationId = intent.getIntExtra("selectedReservationId", 0)
        reservationViewModel.getSingleReservation(selectedReservationId);
        reservationViewModel.singleReservation.observe(this@ShowReservationDetailActivity) {
            val codeText = findViewById<TextView>(R.id.content_reservation_number)
            codeText.text = it.idsl.toString()

            val sportType = findViewById<TextView>(R.id.content_sport_typology)
            sportType.text = it.sport

            val place = findViewById<TextView>(R.id.content_place)
            place.text = it.strut

            val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.data).split(" ")
            val date = formattedDate[0]
            val hour1 = formattedDate[1]
            val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

            val date_time = findViewById<TextView>(R.id.content_date_time)
            date_time.text = date + ", " + hour1 + "-" + hour2

            if (it.note.isNotEmpty() && it.note != R.string.content_notes.toString()) {
                val notes = findViewById<TextView>(R.id.content_notes)
                notes.text = it.note
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
            val intent = Intent(this, EditReservationDetailActivity::class.java)
            intent.putExtra("selectedReservationId", selectedReservationId)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showPopupWindow(view: View) {

        //Create a View object yourself through inflater
        val popupView: View = layoutInflater.inflate(R.layout.popup_deleate_reservation, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        //Initialize the elements of our window, install the handler
        val buttonDelete = popupView.findViewById<Button>(R.id.delete_button)
        buttonDelete.setOnClickListener {
            // elimina reservation
            reservationViewModel.getSingleReservation(selectedReservationId)
            reservationViewModel.singleReservation.observe(this@ShowReservationDetailActivity) {
                reservationViewModel.deleteReservation(it)
                val intent = Intent(this, ReservationActivity::class.java)
                startActivity(intent)
            }
        }

        val buttonCancel = popupView.findViewById<Button>(R.id.close_popup_delete)
        buttonCancel.setOnClickListener {
            popupWindow.dismiss()
        }


        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }
    }
}