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

private lateinit var context : Context

@AndroidEntryPoint
class ShowAllCourtReviews : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val reviewsViewModel by viewModels<ReviewsDBViewModel>()

    //initialize toolbar variables
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_show_all_court_reviews)

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
        val structName = intent.getStringExtra("structName")
        navbarText.text = "Reviews for $structName"

        val idStruct = intent.getStringExtra("idStruct")

        reviewsViewModel.getReviewsByStruct(idStruct!!)

        val loading = findViewById<ProgressBar>(R.id.loading_show_court_reviews)
        val noReviews = findViewById<TextView>(R.id.no_reviews_text)
        noReviews.visibility = View.GONE

        reviewsViewModel.structReviews.observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.list_all_court_reviews)
            recyclerView.adapter = StructReviewsAdapter(it, this)
            recyclerView.layoutManager = LinearLayoutManager(this)

            loading.visibility = View.GONE

            if (it.isEmpty()) {
                noReviews.visibility = View.VISIBLE
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
                        val sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.clear()
                        editor.apply()
                        Log.d("SHAREDPREFERENCES", "cancello le shared preferences")
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

//define recycler view for
class StructReviewsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val structRating = v.findViewById<RatingBar>(R.id.structure_rating)
    val equipRating = v.findViewById<RatingBar>(R.id.equipment_rating)
    val dressRating = v.findViewById<RatingBar>(R.id.dressing_rooms_rating)
    val staffRating = v.findViewById<RatingBar>(R.id.staff_rating)
    val comment = v.findViewById<TextView>(R.id.additional_comment)
}

class StructReviewsAdapter(val listReviews: List<MutableMap<String, Any>>, context: Context ): RecyclerView.Adapter<StructReviewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StructReviewsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.show_court_review_box, parent, false)

        return StructReviewsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listReviews.size
    }

    override fun onBindViewHolder(holder: StructReviewsViewHolder, position: Int) {
        val reservation = listReviews[position]

        holder.structRating.rating = reservation["voto1"].toString().toFloat()
        holder.equipRating.rating = reservation["voto2"].toString().toFloat()
        holder.dressRating.rating = reservation["voto3"].toString().toFloat()
        holder.staffRating.rating = reservation["voto4"].toString().toFloat()
    }
}