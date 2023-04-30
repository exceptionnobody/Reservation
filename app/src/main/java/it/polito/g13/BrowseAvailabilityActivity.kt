package it.polito.g13

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

private lateinit var context : Context
@AndroidEntryPoint
class BrowseAvailabilityActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //instantiate viewModel
    private val posResViewModel by viewModels<PosResViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var calendarView: CustomCalendarView

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

        //get selected sport
        val selectedSport = intent.getStringExtra("selectedSport")

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Book a $selectedSport court"

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

        calendarView.setCalendarListener(object : CalendarListener {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSelected(date: Date) {

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = sdf.parse(sdf.format(date))

                //retrieve available possible reservations
                posResViewModel.getPosRes(selectedSport!!, formattedDate!!)
                //show them in the recycler view
                posResViewModel.listPosRes.observe(this@BrowseAvailabilityActivity) {
                    if (it != null) {
                        val recyclerView = findViewById<RecyclerView>(R.id.bookReservationContainer)
                        recyclerView.adapter = PosResAdapter(it)
                        recyclerView.layoutManager = LinearLayoutManager(this@BrowseAvailabilityActivity)
                    }
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

//define recycler view for possible reservations
class PosResViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val tv = v.findViewById<TextView>(R.id.book_reservation_title)
}

class PosResAdapter(val listPosRes: List<PosRes>): RecyclerView.Adapter<PosResViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosResViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.availability_box, parent, false)

        return PosResViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listPosRes.size
    }

    override fun onBindViewHolder(holder: PosResViewHolder, position: Int) {
        val posRes = listPosRes[position]

        val formattedDate = SimpleDateFormat("yyyy-mm-dd HH:mm").format(posRes.data).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        val txt = posRes.strut + ", " + hour1 + "-" + hour2

        holder.tv.text = txt

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowPosResDetailActivity::class.java)
            intent.putExtra("selectedPosResId", posRes.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}