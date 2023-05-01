package it.polito.g13

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
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
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.viewModel.PosResViewModel
import it.polito.g13.viewModel.ReservationsViewModel
import java.text.SimpleDateFormat
import java.util.*

private var selectedSport: String? = null

@AndroidEntryPoint
class ShowPosResDetailActivity : AppCompatActivity() {
    private lateinit var notesInput: EditText

    private val posResViewModel by viewModels<PosResViewModel>()
    private val reservationViewModel by viewModels<ReservationsViewModel>()

    private var selectedPosResId: Int = 0
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pos_res_detail)

        //get selected sport
        selectedSport = intent.getStringExtra("selectedSport")

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Your reservation detail"

        confirmButton = findViewById(R.id.confirm_button_posres)
        val cancelButton = findViewById<Button>(R.id.cancel_button_posres)

        notesInput = findViewById(R.id.content_notes_posres)

        //get selected reservation
        selectedPosResId = intent.getIntExtra("selectedPosResId", 0)
        posResViewModel.getPosResById(selectedPosResId)
        posResViewModel.singlePosRes.observe(this) {
            val sportText = findViewById<TextView>(R.id.content_sport_typology_posres)
            sportText.text = it.sport

            val placeText = findViewById<TextView>(R.id.content_place_posres)
            placeText.text = it.strut

            val dateTimeText = findViewById<TextView>(R.id.content_date_time_posres)
            val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.data).split(" ")
            val date = formattedDate[0]
            val hour1 = formattedDate[1]
            val hour2 = (hour1.split(":")[0].toInt() + 1).toString() + ":" + hour1.split(":")[1]

            dateTimeText.text = date + ", " + hour1 + "-" + hour2
        }

        confirmButton.setOnClickListener {
            posResViewModel.singlePosRes.observe(this) {
                if (it != null) {
                    reservationViewModel.insertReservation(Reservation(99, it.id, 1, it.strut, it.sport, it.data, notesInput.text.toString(), true))
                    posResViewModel.updatePosRes(PosRes(it.id, it.strut, it.campo, it.sport, it.data, false))

                    val intent = Intent(this, ReservationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, BrowseAvailabilityActivity::class.java)
            intent.putExtra("selectedSport", selectedSport)
            startActivity(intent)
        }
    }
}