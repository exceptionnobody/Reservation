package it.polito.g13

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import com.stacktips.view.utils.CalendarUtils
import it.polito.g13.ui.main.ReservationFragment
import java.util.*


private lateinit var context : Context
class ReservationActivity : AppCompatActivity() {
    lateinit var calendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.activity_reservation)
        calendarView = findViewById(R.id.calendar_view);
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.refreshCalendar(currentCalendar);

        val decorators : MutableList<DayDecorator> = mutableListOf<DayDecorator>()
        decorators.add(DaysWithReservations())

        calendarView.decorators = decorators
        calendarView.refreshCalendar(currentCalendar);

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {

                val reservationBoxContainer = findViewById<LinearLayout>(R.id.reservationBoxContainer)

                reservationBoxContainer.removeAllViews()

                // fare for che inserisce le n prenotzioni per la data selezionata
                // se ho una prenotazione
                if(CalendarUtils.isPastDay(date)) {
                    val bookedReservation = layoutInflater.inflate(R.layout.reservation_box, reservationBoxContainer, false)

                    reservationBoxContainer.addView(bookedReservation)
                } else {
                    val noReservationFounded = layoutInflater.inflate(R.layout.no_reservation, reservationBoxContainer, false)
                    reservationBoxContainer.addView(noReservationFounded)
                }

            }

            override fun onMonthChanged(date: Date) {

            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReservationFragment.newInstance())
                .commitNow()
        }
    }
}
class DaysWithReservations() : DayDecorator {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(dayView: DayView) {
        // invece di past day vedere se ha una reservation
        if (CalendarUtils.isPastDay(dayView.date)) {
            val image : Drawable? = context.getDrawable(R.drawable.bg_calendar_reservation)
            dayView.background = image
        }
    }
}