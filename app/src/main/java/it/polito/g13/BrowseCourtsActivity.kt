package it.polito.g13

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.entities.Reservation
import it.polito.g13.entities.Struttura
import it.polito.g13.entities.review_struct
import it.polito.g13.viewModel.ReservationsViewModel
import it.polito.g13.viewModel.ReviewStructureViewModel
import it.polito.g13.viewModel.StrutturaViewModel

private lateinit var context : Context

@AndroidEntryPoint
class BrowseCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reservationViewModel by viewModels<ReservationsViewModel>()
    val structureViewModel by viewModels<StrutturaViewModel>()
    val reviewStructureViewModel by viewModels<ReviewStructureViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_browse_courts)

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
        navbarText.text = "Select a court to see reviews"

        //use viewmodel to manage recycler view
        /*
        structureViewModel.structures.observe(this) {structures ->
            reservationViewModel.reservations.observe(this) {reservations ->
                val listToReview = reservations.distinctBy { it.strut }
                val listPastReview: MutableList<review_struct> = mutableListOf()

                for (r in listToReview) {
                    val struct = structures.find { it.structure_name == r.strut }

                    reviewStructureViewModel.getReviewByStructureAndUserId(struct?.id!!, 1)

                    reviewStructureViewModel.singleReviewStructure.observe(this) {
                        if (it != null)
                            listPastReview.add(it)
                    }
                }

                val recyclerView = findViewById<RecyclerView>(R.id.list_review_courts)
                recyclerView.adapter = ReviewReservationAdapter(listToReview, structures, listPastReview, this)
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

         */
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
            R.id.nav_browse_courts -> {
                val intent = Intent(this, BrowseCourtsActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

//define recycler view for STRUCTURES
/*
class ReviewReservationsViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val strut = v.findViewById<TextView>(R.id.review_strut)
    val sport = v.findViewById<TextView>(R.id.review_sport)
    val noPastRating = v.findViewById<TextView>(R.id.no_past_rating)
    val avgRating = v.findViewById<RatingBar>(R.id.average_past_rating)
}

class ReviewReservationAdapter(val listReservation: List<Reservation>, val listStructures: List<Struttura>, val listPastReview: List<review_struct>, context: Context ): RecyclerView.Adapter<ReviewReservationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewReservationsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_court_box, parent, false)

        return ReviewReservationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReservation.size
    }

    override fun onBindViewHolder(holder: ReviewReservationsViewHolder, position: Int) {
        val reservation = listReservation[position]

        holder.strut.text = reservation.strut
        holder.sport.text = reservation.sport

        val structure = listStructures.filter { it.structure_name == reservation.strut }[0]

        val review = listPastReview.filter { it.review_id_struct == structure.id }

        if (review.isNotEmpty()) {
            holder.noPastRating.visibility = View.GONE

            val avg = ((review[0].s_q1 + review[0].s_q2 + review[0].s_q3 + review[0].s_q4) / 4).toFloat()

            holder.avgRating.rating = avg
        }
        else {
            holder.avgRating.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowReviewCourtsActivity::class.java)
            intent.putExtra("selectedCourtName", structure.structure_name)
            intent.putExtra("selectedCourtId", structure.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
 */