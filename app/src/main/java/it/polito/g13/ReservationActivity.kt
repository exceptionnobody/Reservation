package it.polito.g13

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.android.material.navigation.NavigationView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import com.stacktips.view.utils.CalendarUtils
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.ui.main.ReservationFragment
import it.polito.g13.viewModel.PosResViewModel
import it.polito.g13.viewModel.ReservationsViewModel
import java.text.SimpleDateFormat
import java.util.*


private lateinit var context : Context
@AndroidEntryPoint
class ReservationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val posResViewModel by viewModels<PosResViewModel>()

    val reservationViewModel by viewModels<ReservationsViewModel> ()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_reservation)

        //creating some data for posRes table
        posResViewModel.insertPosRes(PosRes(1, "Centro sportivo Robilant", 1, "Football", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-30 15:00")!!, true))
        posResViewModel.insertPosRes(PosRes(2, "Sporting Dora", 1, "Football", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-30 17:00")!!, true))
        posResViewModel.insertPosRes(PosRes(3, "Centro sportivo Carmagnola", 1, "Tennis", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-30 12:00")!!, true))
        posResViewModel.insertPosRes(PosRes(4, "Sporting Dora", 1, "Basket", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-05 18:00")!!, true))
        posResViewModel.insertPosRes(PosRes(5, "Centro sportivo Carmagnola", 1, "Volleyball", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-06 19:00")!!, true))

        //creating some data for reservation table
        reservationViewModel.insertReservation(Reservation(1, 19405, 1, "Centro sportivo Robilant", "Football", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-06 14:00")!!, "Need a ball", true ))
        reservationViewModel.insertReservation(Reservation(2, 19406, 1, "Sporting Dora", "Volleyball", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-06 15:00")!!, "", true ))

        reservationViewModel.insertReservation(Reservation(3, 19407, 1, "Sporting Dora", "Volleyball", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse("2023-05-07 19:00")!!, "open field", true ))

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
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservations"

        calendarView = findViewById(R.id.calendar_view);
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.refreshCalendar(currentCalendar);

        reservationViewModel.reservations.observe(this@ReservationActivity) {
            val decorators : MutableList<DayDecorator> = mutableListOf<DayDecorator>()
            decorators.add(DaysWithReservations(it))

            calendarView.decorators = decorators
            calendarView.refreshCalendar(currentCalendar);
        }

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {

                val noReservationBoxContainer = findViewById<LinearLayout>(R.id.noReservationBoxContainer)

                noReservationBoxContainer.removeAllViews()

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = sdf.parse(sdf.format(date))

                //show them in the recycler view
                reservationViewModel.reservations.observe(this@ReservationActivity) {
                    val reservationInDate = it.filter { res -> sdf.parse(sdf.format(res.data)) == formattedDate}
                    val recyclerView = findViewById<RecyclerView>(R.id.reservationBoxContainer)
                    recyclerView.adapter = ReservationAdapter(reservationInDate, this@ReservationActivity)
                    recyclerView.layoutManager = LinearLayoutManager(this@ReservationActivity)
                    if (reservationInDate.isEmpty()) {
                        val noReservationFounded = layoutInflater.inflate(R.layout.no_reservation, noReservationBoxContainer, false)
                        noReservationBoxContainer.addView(noReservationFounded)
                    }
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
            val intent = Intent(this, BrowseAvailabilityActivity::class.java)
            startActivity(intent)
        }

        val showReservationDetail = findViewById<RelativeLayout>(R.id.reservation_box)

        showReservationDetail?.setOnClickListener {
            val intent = Intent(this, ShowReservationDetailActivity::class.java)
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

class DaysWithReservations(reservationList: List<Reservation>) : DayDecorator {
    val reservationToShow = reservationList
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(dayView: DayView) {
        reservationToShow.forEach{reserv -> run {

                val formattedDate1 = SimpleDateFormat("yyyy-MM-dd").format(reserv.data)
                val formattedDate2 = SimpleDateFormat("yyyy-MM-dd").format(dayView.date)

                val date1 = SimpleDateFormat("yyyy-MM-dd").parse(formattedDate1)
                val date2 = SimpleDateFormat("yyyy-MM-dd").parse(formattedDate2)

                if (date1 == date2 && !CalendarUtils.isPastDay(dayView.date)) {
                    val image : Drawable? = context.getDrawable(R.drawable.bg_calendar_reservation)
                    dayView.background = image
                }
            }

        }
    }
}

//define recycler view for reservations
class ReservationViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val tv = v.findViewById<TextView>(R.id.reservation_title)
}

class ReservationAdapter(val listReservation: List<Reservation>, context: Context ): RecyclerView.Adapter<ReservationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reservation_box, parent, false)

        return ReservationViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReservation.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = listReservation[position]

        val formattedDate = SimpleDateFormat("yyyy-mm-dd HH:mm").format(reservation.data).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        val txt = reservation.strut + ", " + reservation.sport + ", " + hour1 + "-" + hour2

        holder.tv.text = txt

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowReservationDetailActivity::class.java)
            intent.putExtra("selectedReservationId", reservation.id)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}