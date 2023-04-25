package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import dagger.hilt.android.AndroidEntryPoint
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import com.stacktips.view.utils.CalendarUtils
import it.polito.g13.ui.main.ReservationFragment
import java.util.*


private lateinit var context : Context
@AndroidEntryPoint
class ReservationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_reservation)

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

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservations"

        calendarView = findViewById(R.id.calendar_view);
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.refreshCalendar(currentCalendar);

        val decorators : MutableList<DayDecorator> = mutableListOf<DayDecorator>()
        decorators.add(DaysWithReservations())

        calendarView.decorators = decorators
        calendarView.refreshCalendar(currentCalendar);

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {

                val reservationBoxContainer = findViewById<LinearLayout>(R.id.reservationBoxContainer)

                reservationBoxContainer.removeAllViews()

                // fare for che inserisce le n prenotzioni per la data selezionata
                // se ho una prenotazione
                if(CalendarUtils.isPastDay(date)) {
                    val bookedReservation = layoutInflater.inflate(R.layout.reservation_box, reservationBoxContainer, false)

                    reservationBoxContainer.addView(bookedReservation)
                }

            }

            override fun onMonthChanged(date: Date) {

            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReservationFragment.newInstance())
                .commitNow()
        }

        val reservationButton = findViewById<Button>(R.id.button)

        reservationButton.setOnClickListener {
            val reservationContainer = findViewById<LinearLayout>(R.id.select_sport_reservation_container)
            val selectSportReservation = layoutInflater.inflate(R.layout.select_sport_availability, reservationContainer, false)

            //spinner for sport
            val sportSpinner = selectSportReservation.findViewById<Spinner>(R.id.selectSport)
            sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

            reservationContainer.addView(selectSportReservation)

            val checkAvailability = findViewById<Button>(R.id.checkAvailabilityButton)
            checkAvailability.setOnClickListener {
                val intent = Intent(this, BrowseAvailabilityActivity::class.java)
                startActivity(intent)
            }

            reservationButton.isClickable = false
            reservationButton.setBackgroundColor(Color.GRAY)
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
class DaysWithReservations() : DayDecorator {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(dayView: DayView) {
        // invece di past day vedere se ha una reservation
        if (CalendarUtils.isPastDay(dayView.date)) {
            val image : Drawable? = context.getDrawable(R.drawable.bg_calendar_reservation)
            dayView.background = image
        }
    }
}