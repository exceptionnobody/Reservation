package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import it.polito.g13.ui.main.ReservationFragment
import java.util.*

class ReservationActivity : AppCompatActivity() {
    lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservations"

        calendarView = findViewById(R.id.calendar_view);
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.refreshCalendar(currentCalendar);
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReservationFragment.newInstance())
                .commitNow()
        }

        val reservationButton = findViewById<Button>(R.id.button)

        reservationButton.setOnClickListener {
            val reservationContainer = findViewById<LinearLayout>(R.id.select_sport_reservation_container)
            val selectSportReservation = layoutInflater.inflate(R.layout.select_sport_availability, reservationContainer, false)

            //spinner for sport
            val sportSpinner = selectSportReservation.findViewById<Spinner>(R.id.selectSport)
            sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

            reservationContainer.addView(selectSportReservation)

            val checkAvailability = findViewById<Button>(R.id.checkAvailabilityButton)
            checkAvailability.setOnClickListener {
                val intent = Intent(this, BrowseAvailabilityActivity::class.java)
                startActivity(intent)
            }

            reservationButton.isClickable = false
            reservationButton.setBackgroundColor(Color.GRAY)
        }
    }
}