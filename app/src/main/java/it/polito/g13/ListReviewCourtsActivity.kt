package it.polito.g13

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.activities.login.LoginActivity
import it.polito.g13.viewModel.ReservationsDBViewModel
import org.w3c.dom.Text

private lateinit var context : Context

@AndroidEntryPoint
class ListReviewCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reservationViewModel by viewModels<ReservationsDBViewModel>()

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

        val menuItemBrowseCourts = navView.menu.findItem(R.id.nav_browse_courts)
        menuItemBrowseCourts.setActionView(R.layout.menu_item_browse_courts)

        val menuItemExit = navView.menu.findItem(R.id.nav_exit)
        menuItemExit.setActionView(R.layout.menu_item_exit)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Review a court"

        val loading = findViewById<ProgressBar>(R.id.loading_list_review_courts)
        val container = findViewById<ScrollView>(R.id.list_review_courts_container)
        val noReservation = findViewById<TextView>(R.id.no_courts)

        reservationViewModel.getUserPastReservations()

        reservationViewModel.userHasPastReservations.observe(this) {userHasPastReservations ->
            noReservation.visibility = View.GONE
            loading.visibility = View.VISIBLE

            if (userHasPastReservations) {
                reservationViewModel.userReservations.observe(this) {reservations ->
                    val listToReview = reservations.distinctBy { it["nomestruttura"] }

                    val recyclerView = findViewById<RecyclerView>(R.id.list_review_courts)
                    recyclerView.adapter = ReviewReservationAdapter(listToReview, this)
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    loading.visibility = View.GONE
                    container.visibility = View.VISIBLE
                }
            }
            else {
                loading.visibility = View.GONE
                noReservation.visibility = View.VISIBLE
            }
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
            R.id.nav_browse_courts -> {
                val intent = Intent(this, BrowseCourtsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_exit -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // ...
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
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
    val city = v.findViewById<TextView>(R.id.review_city)
    val noPastRating = v.findViewById<TextView>(R.id.no_past_rating)
    val avgRating = v.findViewById<RatingBar>(R.id.average_past_rating)
}

class ReviewReservationAdapter(val listReservation: List<MutableMap<String, Any>>, context: Context ): RecyclerView.Adapter<ReviewReservationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewReservationsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_court_box, parent, false)

        return ReviewReservationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReservation.size
    }

    override fun onBindViewHolder(holder: ReviewReservationsViewHolder, position: Int) {
        val reservation = listReservation[position]

        val structId = reservation["idstruttura"].toString()

        val structName = reservation["nomestruttura"].toString()
        val sport = reservation["tiposport"].toString()
        val city = reservation["citta"].toString()
        val avg = reservation["avg"]

        holder.strut.text = structName
        holder.sport.text = sport
        holder.city.text = city

        if (avg != null) {
            holder.noPastRating.visibility = View.GONE
            holder.avgRating.rating = avg as Float
        }
        else {
            holder.avgRating.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowReviewCourtsActivity::class.java)
            intent.putExtra("selectedCourtName", structName)
            intent.putExtra("selectedCourtId", structId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}