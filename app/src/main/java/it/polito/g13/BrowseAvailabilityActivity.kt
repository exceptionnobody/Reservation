package it.polito.g13

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import com.stacktips.view.utils.CalendarUtils
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.PosRes
import it.polito.g13.ui.main.ReservationFragment
import it.polito.g13.viewModel.PosResViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BrowseAvailabilityActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val posResViewModel by viewModels<PosResViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_availability)

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
        navbarText.text = "Book a reservation"

        calendarView = findViewById(R.id.book_reservation_calendar_view)

        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.firstDayOfWeek = Calendar.MONDAY
        calendarView.setShowOverflowDate(true)
        calendarView.refreshCalendar(currentCalendar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerBookReservation, ReservationFragment.newInstance())
                .commitNow()
        }

        //get selected sport
        val selectedSport = intent.getStringExtra("selectedSport")

        calendarView.setCalendarListener(object : CalendarListener {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSelected(date: Date) {
                //select LinearLayout to be inflated
                val bookReservationContainer = findViewById<LinearLayout>(R.id.bookReservationContainer)

                //clean the view removing previously showed reservations for another date
                bookReservationContainer.removeAllViews()

                val today = Calendar.getInstance().time

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = sdf.parse(sdf.format(date))
                val formattedToday = sdf.parse(sdf.format(today))

                if(formattedDate >= formattedToday) {
                    posResViewModel.getPosRes(selectedSport!!, formattedDate!!)

                    posResViewModel.listPosRes.observe(this@BrowseAvailabilityActivity) {
                        if (it != null) {
                            for (posRes in it) {
                                val res = layoutInflater.inflate(R.layout.availability_box, bookReservationContainer, false)
                                res.findViewById<TextView>(R.id.book_reservation_title).setText(posRes.strut + ", " + posRes.sport)
                                bookReservationContainer.addView(res)
                            }
                        }
                    }
                }
                else {
                    //clean the view removing previously showed reservations for another date
                    bookReservationContainer.removeAllViews()
                }
            }

            override fun onMonthChanged(date: Date) {
                //do nothing
            }
        })

        //adding calendar day decorators
        val decorators: MutableList<DayDecorator> = ArrayList()
        decorators.add(DisabledColorDecorator())
        calendarView.decorators = decorators
        calendarView.refreshCalendar(currentCalendar)
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

    open class DisabledColorDecorator : DayDecorator {
        override fun decorate(dayView: DayView) {
            if (CalendarUtils.isPastDay(dayView.date)) {
                dayView.setTextColor(Color.GRAY)
            }
        }
    }
}