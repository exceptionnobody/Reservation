package it.polito.g13.activities.editprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.g13.R
import it.polito.g13.firebase_objects.ProfileUser

val sports = mutableListOf("Basket", "Football", "Padel", "Rugby", "Tennis", "Volleyball")
val selectedSportsLevel = mutableMapOf<String, String>()
val selectedSportsAchievement = mutableMapOf<String, String>()
val sportLevels = listOf("Beginner", "Intermediate", "Professional")

class SportsActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    var num_sports = 0

    private lateinit var addSportTextContainer: RelativeLayout
    private lateinit var addSportIcon: FloatingActionButton
    private lateinit var addSportText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sports)

        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Edit profile"

        //get user data from intent
        val myUser = intent.getSerializableExtra("myUser") as ProfileUser

        //add a new sport
        addSportTextContainer = findViewById(R.id.addSportTextContainer)
        addSportIcon = findViewById(R.id.addSportIcon)
        addSportText = findViewById(R.id.addSport)

        num_sports = 0

        addSportTextContainer.setOnClickListener {
            if(num_sports < sports.size){
                handleNewSport()
                num_sports++

                addSportIcon.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                addSportText.setTextColor(Color.GRAY)

                addSportTextContainer.isClickable = false
                addSportIcon.isClickable = false
            }
        }

        addSportIcon.setOnClickListener {
            if(num_sports < sports.size){
                handleNewSport()
                num_sports++

                addSportIcon.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                addSportText.setTextColor(Color.GRAY)

                addSportTextContainer.isClickable = false
                addSportIcon.isClickable = false
            }
        }

        val confirmButton = findViewById<Button>(R.id.confirm_button)

        confirmButton.setOnClickListener {
            insertUserProfile(myUser)
        }
    }

    //handle adding new sport
    @SuppressLint("InflateParams")
    private fun handleNewSport() {
        val addSportContainer = findViewById<LinearLayout>(R.id.sportsContainer)

        val sportList = layoutInflater.inflate(R.layout.add_new_sport, addSportContainer, false)

        //spinner for sport
        val sportSpinner = sportList.findViewById<Spinner>(R.id.editGames)
        sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

        //spinner for sport level
        val sportLevelSpinner = sportList.findViewById<Spinner>(R.id.editGameLevel)
        sportLevelSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sportLevels)

        addSportContainer.addView(sportList)

        var sport = ""

        //handle inserted sport
        sportSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sport = sportSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //do nothing
            }
        }

        var level = ""

        //handle inserted sport level
        sportLevelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                level = sportLevelSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //do nothing
            }
        }

        //handle inserted sport achievement
        val sportAchievement = sportList.findViewById<TextInputEditText>(R.id.editDescription)
        var achievement = ""

        sportAchievement.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                achievement = p0.toString()
            }
        })

        //confirm inserted sport
        val confirmSportIcon = sportList.findViewById<FloatingActionButton>(R.id.confirm_sport)
        confirmSportIcon.setOnClickListener {
            selectedSportsLevel[sport] = level
            selectedSportsAchievement[sport] = achievement
            sports.remove(sport)

            addSportIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5722"))
            addSportText.setTextColor(Color.parseColor("#FF5722"))

            addSportTextContainer.isClickable = true
            addSportIcon.isClickable = true

            confirmSportIcon.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            confirmSportIcon.isClickable = false
        }

        //delete inserted sport
        val deleteSportIcon = sportList.findViewById<FloatingActionButton>(R.id.delete_sport)
        deleteSportIcon.setOnClickListener {
            addSportContainer.removeView(sportList)

            selectedSportsLevel.remove(sport)
            selectedSportsAchievement.remove(sport)
            sports.add(sport)

            addSportIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5722"))
            addSportText.setTextColor(Color.parseColor("#FF5722"))

            addSportTextContainer.isClickable = true
            addSportIcon.isClickable = true

            num_sports--
        }
    }

    private fun insertUserProfile(myuser: ProfileUser) {
        if (selectedSportsLevel.containsKey("Basket")) {
            myuser.basketLevel = selectedSportsLevel["Basket"].toString()
            myuser.basketAchievements = selectedSportsAchievement["Basket"].toString()
        }
        if (selectedSportsLevel.containsKey("Football")) {
            myuser.footballLevel = selectedSportsLevel["Football"].toString()
            myuser.footballAchievements = selectedSportsAchievement["Football"].toString()
        }
        if (selectedSportsLevel.containsKey("Padel")) {
            myuser.padelLevel = selectedSportsLevel["Padel"].toString()
            myuser.padelAchievements = selectedSportsAchievement["Padel"].toString()
        }
        if (selectedSportsLevel.containsKey("Rugby")) {
            myuser.rugbyLevel = selectedSportsLevel["Rugby"].toString()
            myuser.rugbyAchievements = selectedSportsAchievement["Rugby"].toString()
        }
        if (selectedSportsLevel.containsKey("Tennis")) {
            myuser.tennisLevel = selectedSportsLevel["Tennis"].toString()
            myuser.tennisAchievements = selectedSportsAchievement["Tennis"].toString()
        }
        if (selectedSportsLevel.containsKey("Volleyball")) {
            myuser.volleyballLevel = selectedSportsLevel["Volleyball"].toString()
            myuser.volleyballAchievements = selectedSportsAchievement["Volleyball"].toString()
        }

        val user = db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("profile").document("info")
        user.set(myuser)
            .addOnCompleteListener {

                val intent = Intent(this, ShowProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                val t = Toast.makeText(this, "Confirmed", Toast.LENGTH_SHORT)
                t.show()
                finish()
            }
    }
}