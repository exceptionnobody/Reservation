package it.polito.g13

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.utils.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

val sportCenters = listOf("Centro sportivo Robilant", "Sporting Dora", "Campo sportivo Carmagnola", "Impianto sportivo Roveda")

class EditReservationDetailActivity : AppCompatActivity() {
    lateinit var sportSpinner : Spinner
    lateinit var sportCentersSpinner: Spinner

    private lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_reservation_detail)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservation detail"

        val confirmButton = findViewById<Button>(R.id.confirm_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        sportSpinner= findViewById(R.id.content_sport_typology)

        sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

        sportCentersSpinner= findViewById(R.id.content_place)

        sportCentersSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sportCenters)

        calendarView = findViewById(R.id.content_date_time)

        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.firstDayOfWeek = Calendar.MONDAY
        calendarView.setShowOverflowDate(true)
        calendarView.refreshCalendar(currentCalendar)

        //adding calendar day decorators
        val decorators: MutableList<DayDecorator> = ArrayList()
        decorators.add(BrowseAvailabilityActivity.DisabledColorDecorator())
        calendarView.decorators = decorators
        calendarView.refreshCalendar(currentCalendar)


        // preselezionare data
        /*val newCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        newCalendar.add(Calendar.DATE, 1)

        calendarView.markDayAsSelectedDay(newCalendar.time)
        calendarView.refreshCalendar(currentCalendar)*/

        // se qualcosa non è selezionato
        if (true) {
            confirmButton.isClickable = false
            confirmButton.setBackgroundColor(Color.GRAY)
        }

        confirmButton.setOnClickListener {
            // confermare scelte
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, ShowReservationDetailActivity::class.java)
            startActivity(intent)
        }

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {

                if(!CalendarUtils.isPastDay(date)) {
                    confirmButton.isClickable = true
                    confirmButton.setBackgroundColor(resources.getColor(R.color.primary_green))
                } else {
                    // controllare che tutto sia selezionato
                    confirmButton.isClickable = false
                    confirmButton.setBackgroundColor(Color.GRAY)
                }
            }

            override fun onMonthChanged(date: Date) {

            }
        })
    }

}