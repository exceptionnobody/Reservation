package it.polito.g13

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.utils.CalendarUtils
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.viewModel.PosResViewModel
import it.polito.g13.viewModel.ReservationsViewModel
import java.text.SimpleDateFormat
import java.util.*

val sportCenters = listOf("Centro sportivo Robilant", "Sporting Dora", "Centro sportivo Carmagnola", "Impianto sportivo Roveda")

private var newPosResId: Int? = 0

@AndroidEntryPoint
//class EditReservationDetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
class EditReservationDetailActivity : AppCompatActivity() {
    //lateinit var sportSpinner : Spinner
    //lateinit var sportCentersSpinner: Spinner
    private lateinit var notesInput: EditText

    private lateinit var calendarView: CustomCalendarView

    private val reservationViewModel by viewModels<ReservationsViewModel> ()
    private val posResViewModel by viewModels<PosResViewModel>()
    private var selectedReservationId: Int = 0
    private lateinit var selectedDate: Date
    private lateinit var structureName: String
    private lateinit var sportName: String
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_reservation_detail)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservation detail"

        confirmButton = findViewById<Button>(R.id.confirm_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        /*sportSpinner = findViewById(R.id.content_sport_typology)

        sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

        sportSpinner.onItemSelectedListener = this@EditReservationDetailActivity

        sportCentersSpinner = findViewById(R.id.content_place)

        sportCentersSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sportCenters)

        sportCentersSpinner.onItemSelectedListener = this@EditReservationDetailActivity*/

        notesInput = findViewById(R.id.content_notes)

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

        // la data non è selezionata
        confirmButton.isClickable = false
        confirmButton.setBackgroundColor(Color.GRAY)

        confirmButton.setOnClickListener {
            if (newPosResId != null && newPosResId != 0) {
                posResViewModel.getPosResById(newPosResId!!)
                posResViewModel.singlePosRes.observe(this@EditReservationDetailActivity) {
                    if (it != null) {
                        reservationViewModel.updateReservation(selectedReservationId, it.data)
                        val intent = Intent(this, ShowReservationDetailActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, ShowReservationDetailActivity::class.java)
            startActivity(intent)
        }

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date) {
                selectedDate = date

                retrieveNewReservations(date)

            }

            override fun onMonthChanged(date: Date) {

            }
        })

        //get selected reservation
        selectedReservationId = intent.getIntExtra("selectedReservationId", 0)
        reservationViewModel.getSingleReservation(selectedReservationId);
        reservationViewModel.singleReservation.observe(this@EditReservationDetailActivity) {
            val codeText = findViewById<TextView>(R.id.content_reservation_number)
            codeText.text = it.idsl.toString()

            val sportText = findViewById<TextView>(R.id.content_sport_typology)
            sportText.text = it.sport
            sportName = it.sport

            val placeText = findViewById<TextView>(R.id.content_place)
            placeText.text = it.strut
            structureName = it.strut

            /*val sportSelectedIndex = sports.indexOf(it.sport)
            sportSpinner.setSelection(sportSelectedIndex)

            val placeSelectedIndex = sportCenters.indexOf(it.strut)
            sportCentersSpinner.setSelection(placeSelectedIndex)*/

            if (it.note.isNotEmpty() && it.note != R.string.int_content_notes.toString()) {
                notesInput.setText(it.note)
            }
        }

    }

    fun retrieveNewReservations(date: Date) {
        if (date != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = sdf.parse(sdf.format(date))

            //retrieve available reservations
            //posResViewModel.getPosResByStructure(sportSpinner.selectedItem.toString()!!, formattedDate!!, sportCentersSpinner.selectedItem.toString())

            posResViewModel.getPosResByStructure(sportName!!, formattedDate!!, structureName)
            //show them in the recycler view
            posResViewModel.listPosRes.observe(this@EditReservationDetailActivity) {
                val noAvailability = findViewById<LinearLayout>(R.id.no_availability)

                noAvailability.removeAllViews()

                val recyclerView = findViewById<RecyclerView>(R.id.availability_recyclerView)
                recyclerView.adapter = AvailableSlotAdapter(it, confirmButton, resources)
                recyclerView.layoutManager = LinearLayoutManager(this@EditReservationDetailActivity)
                if (it.isEmpty()) {
                    val noAvailabilityFounded = layoutInflater.inflate(R.layout.no_availability, noAvailability, false)
                    noAvailability.addView(noAvailabilityFounded)
                }
            }

            newPosResId = null

            if (this::confirmButton.isInitialized) {
                confirmButton.isClickable = false
                confirmButton.setBackgroundColor(Color.GRAY)
            }
        }
    }

    /*override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (this::selectedDate.isInitialized && this::structureName.isInitialized && this::sportName.isInitialized) {
            retrieveNewReservations(selectedDate)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // nothing
    }*/
}

//define recycler view for availability reservations
class AvailableSlotViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val tv = v.findViewById<TextView>(R.id.book_reservation_title)
}

class AvailableSlotAdapter(val listAvailableReservation: List<PosRes>, val confirmButton: Button, val resources: Resources): RecyclerView.Adapter<AvailableSlotViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableSlotViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.availability_box, parent, false)

        return AvailableSlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listAvailableReservation.size
    }

    override fun onBindViewHolder(holder: AvailableSlotViewHolder, position: Int) {
        val availableReservation = listAvailableReservation[position]

        val formattedDate = SimpleDateFormat("yyyy-mm-dd HH:mm").format(availableReservation.data).split(" ")
        val hour1 = formattedDate[1]
        val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

        val txt = availableReservation.strut + ", " + availableReservation.sport + ", " + hour1 + "-" + hour2

        holder.tv.text = txt

        holder.itemView.setOnClickListener {
            newPosResId = availableReservation.id
            confirmButton.isClickable = true
            confirmButton.setBackgroundColor(resources.getColor(R.color.primary_green))
            holder.itemView.findViewById<RelativeLayout>(R.id.reservation_box).background = resources.getDrawable(R.drawable.bg_reservation_box_selected)
        }
    }
}