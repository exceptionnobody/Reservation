package it.polito.g13

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.Reservation
import it.polito.g13.viewModel.ReservationsViewModel
import java.text.SimpleDateFormat

private lateinit var context : Context

@AndroidEntryPoint
class ListReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reservationViewModel by viewModels<ReservationsViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_list_review_courts)

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
        navbarText.text = "Review a court"

        reservationViewModel.reservations.observe(this) {
            val recyclerView = findViewById<RecyclerView>(R.id.list_review_courts)
            recyclerView.adapter = ReviewReservationAdapter(it, this)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

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

//define recycler view for reservations
class ReviewReservationsViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val strut = v.findViewById<TextView>(R.id.review_strut)
    val sport = v.findViewById<TextView>(R.id.review_sport)
    val data = v.findViewById<TextView>(R.id.review_data)
    val note = v.findViewById<TextView>(R.id.review_note)
}

class ReviewReservationAdapter(val listReservation: List<Reservation>, context: Context ): RecyclerView.Adapter<ReviewReservationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewReservationsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_court_box, parent, false)

        return ReviewReservationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReservation.size
    }

    override fun onBindViewHolder(holder: ReviewReservationsViewHolder, position: Int) {
        val reservation = listReservation[position]

        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(reservation.data).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        holder.data.text = formattedDate[0] + ", " + hour1 + "-" + hour2
        holder.strut.text = reservation.strut
        holder.sport.text = reservation.sport
        holder.note.text = reservation.note.ifEmpty { "No extra note inserted" }
    }
}