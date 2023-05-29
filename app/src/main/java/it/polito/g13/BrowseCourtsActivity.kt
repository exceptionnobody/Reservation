package it.polito.g13

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
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
import it.polito.g13.viewModel.ReviewsDBViewModel
import it.polito.g13.viewModel.StructuresDBViewModel

private lateinit var context : Context

@AndroidEntryPoint
class BrowseCourtsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val structuresViewModel by viewModels<StructuresDBViewModel>()
    val reviewsViewModel by viewModels<ReviewsDBViewModel>()

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
        menuItemBrowseCourts.setActionView(R.layout.menu_item_browse_courts)

        val menuItemExit = navView.menu.findItem(R.id.nav_exit)
        menuItemExit.setActionView(R.layout.menu_item_exit)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Select a court to see reviews"

        val loading = findViewById<ProgressBar>(R.id.loading_browse_courts)

        structuresViewModel.courts.observe(this) {courts ->
            val recyclerView = findViewById<RecyclerView>(R.id.list_browse_courts)
            recyclerView.adapter = BrowseCourtsAdapter(courts, this)
            recyclerView.layoutManager = LinearLayoutManager(this)

            loading.visibility = View.GONE
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
}

//define recycler view
class BrowseCourtsViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val strut = v.findViewById<TextView>(R.id.browse_strut)
    val sport = v.findViewById<TextView>(R.id.browse_sport)
    val city = v.findViewById<TextView>(R.id.browse_city)
    val noPastRating = v.findViewById<TextView>(R.id.no_past_rating_browse)
    val avgRating = v.findViewById<RatingBar>(R.id.average_past_rating_browse)
}

class BrowseCourtsAdapter(
    val listCourts: List<MutableMap<String, Any>>, context: Context) : RecyclerView.Adapter<BrowseCourtsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseCourtsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.browse_court_box, parent, false)

        return BrowseCourtsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listCourts.size
    }

    override fun onBindViewHolder(holder: BrowseCourtsViewHolder, position: Int) {
        val court = listCourts[position]

        holder.strut.text = court["nomestruttura"].toString()
        holder.sport.text = court["tiposport"].toString()
        holder.city.text = court["citta"].toString()

        if (court["avg"] != null) {
            holder.noPastRating.visibility = View.GONE
            holder.avgRating.rating = court["avg"] as Float

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ShowAllCourtReviews::class.java)
                intent.putExtra("structName", court["nomestruttura"].toString())
                intent.putExtra("idStruct", court["idstruttura"].toString())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        else {
            holder.avgRating.visibility = View.GONE
        }
    }
}