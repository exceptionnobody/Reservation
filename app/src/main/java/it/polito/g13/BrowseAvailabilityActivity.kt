package it.polito.g13

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import it.polito.g13.activities.editprofile.sports
import it.polito.g13.activities.login.LoginActivity
import it.polito.g13.ui.main.ReservationFragment
import it.polito.g13.viewModel.PosResDBViewModel
import java.text.SimpleDateFormat
import java.util.*

private lateinit var context : Context
private var selectedSport: String? = null
private lateinit var structureList:  List<MutableMap<String, Any>>

@AndroidEntryPoint
class BrowseAvailabilityActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //instantiate viewModel
    private val posResViewModel by viewModels<PosResDBViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    //time picker variables initialization
    private lateinit var from_time_picker_availability: TimePickerDialog
    private lateinit var select_from_time_availability: TextView
    private lateinit var from_time_availability: String
    private lateinit var to_time_picker_availability: TimePickerDialog
    private lateinit var select_to_time_availability: TextView
    private lateinit var to_time_availability: String

    private lateinit var calendarView: CustomCalendarView
    private var daySelected: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
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
        navbarText.text = "Book your reservation"

        //set time picker
        select_from_time_availability = findViewById(R.id.select_from_time_availability)

        select_from_time_availability.setOnClickListener(View.OnClickListener {
            val cldr = Calendar.getInstance()
            var hour = cldr[Calendar.HOUR_OF_DAY]
            var minutes = cldr[Calendar.MINUTE]

            if (this::from_time_availability.isInitialized) {
                hour = from_time_availability.split(":")[0].toInt()
                minutes = from_time_availability.split(":")[1].toInt()
            }

            // time picker dialog
            from_time_picker_availability = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var hourFormatted = hourOfDay.toString()
                    var minuteFormatted = minute.toString()
                    if (hourOfDay < 10) {
                        hourFormatted = "0$hourFormatted"
                    }

                    if (minute < 10) {
                        minuteFormatted = "0$minute"
                    }
                    select_from_time_availability.text = String.format("%s:%s", hourFormatted, minuteFormatted)
                }
            }, hour, minutes, true)

            from_time_picker_availability.show()
        })

        select_to_time_availability = findViewById(R.id.select_to_time_availability)

        select_to_time_availability.setOnClickListener(View.OnClickListener {
            val cldr = Calendar.getInstance()
            var hour = cldr[Calendar.HOUR_OF_DAY]
            var minutes = cldr[Calendar.MINUTE]

            if (this::to_time_availability.isInitialized) {
                hour = to_time_availability.split(":")[0].toInt()
                minutes = to_time_availability.split(":")[1].toInt()
            }

            // time picker dialog
            to_time_picker_availability = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var hourFormatted = hourOfDay.toString()
                    var minuteFormatted = minute.toString()
                    if (hourOfDay < 10) {
                        hourFormatted = "0$hourFormatted"
                    }

                    if (minute < 10) {
                        minuteFormatted = "0$minute"
                    }

                    select_to_time_availability.text = String.format("%s:%s", hourFormatted, minuteFormatted)
                }
            }, hour, minutes, true)

            to_time_picker_availability.show()
        })

        //set calendar
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

        //spinner for sport
        val sportSpinner = findViewById<Spinner>(R.id.selectSport)
        sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

        val checkAvailability = findViewById<Button>(R.id.checkAvailabilityButton)

        val cityInput = findViewById<TextView>(R.id.selectCity)

        checkAvailability.setOnClickListener {
            selectedSport = sportSpinner.selectedItem.toString()

            val from = select_from_time_availability.text.ifEmpty { "00:00" }.toString()
            val to = select_to_time_availability.text.ifEmpty { "23:59" }.toString()

            daySelected = null
            if (cityInput.text.toString().isEmpty()) {
                cityInput.background = getDrawable(R.drawable.edit_fields_error)
            } else {
                cityInput.background = getDrawable(R.drawable.edit_fields)
                calendarView.visibility = View.VISIBLE
                posResViewModel.getPostResBySportTimeCity(selectedSport!!, from, to, cityInput.text.toString())
            }
        }

        val loadingCalendar = findViewById<ProgressBar>(R.id.loading_browse_availability_calendar)

        posResViewModel.listPosRes.observe(this) {
            loadingCalendar.visibility = View.VISIBLE

            val decorators : MutableList<DayDecorator> = mutableListOf()
            decorators.add(DaysWithPosRes(it))

            calendarView.decorators = decorators
            calendarView.refreshCalendar(currentCalendar)

            loadingCalendar.visibility = View.GONE
        }

        val loadingDetails = findViewById<ProgressBar>(R.id.loading_browse_availability_details)

        calendarView.setCalendarListener(object : CalendarListener {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSelected(date: Date) {
                loadingDetails.visibility = View.VISIBLE

                daySelected = date
                val noPosResBoxContainer = findViewById<LinearLayout>(R.id.noPosResBoxContainer)

                noPosResBoxContainer.removeAllViews()

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = sdf.parse(sdf.format(date))
                val today = Calendar.getInstance().time
                val formattedToday = sdf.parse(sdf.format(today))
                val titleBookReservation = findViewById<TextView>(R.id.titleBookReservation)
                val titleJoinReservation = findViewById<TextView>(R.id.titleJoinReservation)
                val recyclerViewBook = findViewById<RecyclerView>(R.id.bookReservationContainer)
                val recyclerViewJoin = findViewById<RecyclerView>(R.id.joinReservationContainer)


                titleBookReservation.visibility = View.GONE
                titleJoinReservation.visibility = View.GONE
                recyclerViewBook.visibility = View.GONE
                recyclerViewJoin.visibility = View.GONE
                noPosResBoxContainer.visibility = View.GONE

                if (formattedDate >= formattedToday) {
                    //show them in the recycler view
                    posResViewModel.listPosRes.observe(this@BrowseAvailabilityActivity) {
                        noPosResBoxContainer.removeAllViews()
                        titleBookReservation.visibility = View.GONE
                        titleJoinReservation.visibility = View.GONE
                        recyclerViewBook.visibility = View.GONE
                        recyclerViewJoin.visibility = View.GONE
                        noPosResBoxContainer.visibility = View.GONE
                        val posResInDate = it.filter { posRes ->
                            val timestamp = posRes["data"] as com.google.firebase.Timestamp
                            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                            val netDate = Date(milliseconds)
                            sdf.parse(sdf.format(netDate)) == formattedDate
                        }
                        val posResNew = posResInDate.filter { posRes ->
                            posRes["numberOfCurrentPlayers"] == null || posRes["numberOfCurrentPlayers"].toString() == "0"
                        }
                        val posResJoin = posResInDate.filter { posRes ->
                            posRes["numberOfCurrentPlayers"].toString() != "0"
                        }

                        if (daySelected != null && posResJoin.size > 0) {
                            titleJoinReservation.visibility = View.VISIBLE
                            recyclerViewJoin.visibility = View.VISIBLE
                        }
                        if (daySelected != null && posResNew.size > 0) {
                            titleBookReservation.visibility = View.VISIBLE
                            recyclerViewBook.visibility = View.VISIBLE
                        }
                        recyclerViewBook.adapter = PosResAdapter(posResNew)
                        recyclerViewBook.layoutManager = LinearLayoutManager(this@BrowseAvailabilityActivity)
                        recyclerViewJoin.adapter = PosResAdapter(posResJoin)
                        recyclerViewJoin.layoutManager = LinearLayoutManager(this@BrowseAvailabilityActivity)
                        if (posResNew.isEmpty() && posResJoin.isEmpty() && daySelected != null) {
                            noPosResBoxContainer.visibility = View.VISIBLE
                            val noReservationFounded = layoutInflater.inflate(R.layout.no_reservation, noPosResBoxContainer, false)
                            noReservationFounded.findViewById<TextView>(R.id.textView).text = "No possible reservation for today"
                            noPosResBoxContainer.addView(noReservationFounded)
                        }
                    }
                } else {
                    noPosResBoxContainer.visibility = View.VISIBLE
                    val noReservationFounded = layoutInflater.inflate(R.layout.no_reservation, noPosResBoxContainer, false)
                    noReservationFounded.findViewById<TextView>(R.id.textView).text = "No possible reservation for today"
                    noPosResBoxContainer.addView(noReservationFounded)
                }

                loadingDetails.visibility = View.GONE
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

    open class DisabledColorDecorator : DayDecorator {
        override fun decorate(dayView: DayView) {
            if (CalendarUtils.isPastDay(dayView.date)) {
                dayView.setTextColor(Color.GRAY)
            }
        }
    }
}

//define recycler view for possible reservations
class PosResViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val tv = v.findViewById<TextView>(R.id.book_reservation_title)
    val players = v.findViewById<TextView>(R.id.joined_users_book_reservation)
}

class PosResAdapter(val listPosRes: List<MutableMap<String, Any>>): RecyclerView.Adapter<PosResViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosResViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.availability_box, parent, false)

        return PosResViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listPosRes.size
    }

    override fun onBindViewHolder(holder: PosResViewHolder, position: Int) {
        val posRes = listPosRes[position]

        val timestamp = posRes["data"] as com.google.firebase.Timestamp
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val formattedDate = SimpleDateFormat("yyyy-mm-dd HH:mm").format(netDate).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        val txt = posRes["nomestruttura"].toString() + ", " + hour1 + "-" + hour2
        val txtPlayers = "${posRes["numberOfCurrentPlayers"].toString()}/${posRes["maxpeople"].toString()}"

        holder.tv.text = txt
        holder.players.text = txtPlayers

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowPosResDetailActivity::class.java)
            intent.putExtra("selectedPosResId", posRes["posresid"].toString())
            intent.putExtra("selectedSport", selectedSport)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

class DaysWithPosRes(posResList: List<MutableMap<String, Any>>) : DayDecorator {
    val posResToShow = posResList
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(dayView: DayView) {
        posResToShow.forEach{posRes -> run {
            val timestamp = posRes["data"] as com.google.firebase.Timestamp
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