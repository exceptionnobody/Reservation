package it.polito.g13

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
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
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import com.stacktips.view.utils.CalendarUtils
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.activities.login.LoginActivity

import it.polito.g13.ui.main.ReservationFragment
import it.polito.g13.viewModel.ReservationsDBViewModel

import java.text.SimpleDateFormat
import java.util.*


private lateinit var context : Context
@AndroidEntryPoint
class ReservationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reservationViewModel by viewModels<ReservationsDBViewModel> ()

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
        navbarText.text = "Your reservations"

        calendarView = findViewById(R.id.calendar_view)
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.setFirstDayOfWeek(Calendar.MONDAY)
        calendarView.setShowOverflowDate(true)
        calendarView.refreshCalendar(currentCalendar)

        val loadingCalendar = findViewById<ProgressBar>(R.id.loading_reservations_calendar)

        reservationViewModel.reservations.observe(this@ReservationActivity) {
            val decorators : MutableList<DayDecorator> = mutableListOf<DayDecorator>()
            decorators.add(DaysWithReservations(it))

            calendarView.decorators = decorators
            calendarView.refreshCalendar(currentCalendar)

            loadingCalendar.visibility = View.GONE
        }

        val loadingReservations = findViewById<ProgressBar>(R.id.loading_reservations_list)

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {

                loadingReservations.visibility = View.VISIBLE

                val noReservationBoxContainer = findViewById<LinearLayout>(R.id.noReservationBoxContainer)

                noReservationBoxContainer.removeAllViews()

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = sdf.parse(sdf.format(date))

                //show them in the recycler view
                reservationViewModel.reservations.observe(this@ReservationActivity) {
                    val reservationInDate = it.filter { res ->
                        val timestamp = res["data"] as com.google.firebase.Timestamp
                        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                        val netDate = Date(milliseconds)
                        sdf.parse(sdf.format(netDate)) == formattedDate
                    }
                    val recyclerView = findViewById<RecyclerView>(R.id.reservationBoxContainer)
                    recyclerView.adapter = ReservationAdapter(reservationInDate, this@ReservationActivity)
                    recyclerView.layoutManager = LinearLayoutManager(this@ReservationActivity)
                    if (reservationInDate.isEmpty()) {
                        val noReservationFounded = layoutInflater.inflate(R.layout.no_reservation, noReservationBoxContainer, false)
                        noReservationBoxContainer.addView(noReservationFounded)
                    }
                }

                loadingReservations.visibility = View.GONE
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

        /*
        val db = Firebase.firestore

        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to Date()
        )

// Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }

         */


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
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

class DaysWithReservations(reservationList: List<MutableMap<String, Any>>) : DayDecorator {
    val reservationToShow = reservationList
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(dayView: DayView) {
        reservationToShow.forEach{reserv -> run {

                val timestamp = reserv["data"] as com.google.firebase.Timestamp
                val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                val netDate = Date(milliseconds)
                val formattedDate1 = SimpleDateFormat("yyyy-MM-dd").format(netDate)
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

class ReservationAdapter(val listReservation: List<MutableMap<String, Any>>, context: Context ): RecyclerView.Adapter<ReservationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reservation_box, parent, false)

        return ReservationViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReservation.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = listReservation[position]

        val timestamp = reservation["data"] as com.google.firebase.Timestamp
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val formattedDate = SimpleDateFormat("yyyy-mm-dd HH:mm").format(netDate).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        val txt = reservation["nomestruttura"].toString() + ", " + reservation["tiposport"] + ", " + hour1 + "-" + hour2

        holder.tv.text = txt

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowReservationDetailActivity::class.java)
            intent.putExtra("selectedReservationId", reservation["reservationid"].toString())
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}