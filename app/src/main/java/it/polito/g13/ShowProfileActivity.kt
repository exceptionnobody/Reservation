package it.polito.g13



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toDrawable
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.viewModel.PosResViewModel
import it.polito.g13.viewModel.ReservationsViewModel
import org.json.JSONObject
import java.util.*
import java.util.Date


@AndroidEntryPoint
class ShowProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // TEST VIEWMODEL RESERVATIONS
    //private val resViewModel: ReservationsViewModel by viewModels()

    // TEST VIEWMODEL POSRES
    //private val posResViewModel by viewModels<PosResViewModel>()

    //initialize toolbar variables
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var sharedPreference:SharedPreferences
    lateinit var user_image: ImageView
    lateinit var user_name: TextView //= null//: String= ""
    lateinit var user_nickname: TextView //= null//:String= ""
    lateinit var user_age: TextView //= null//:Int =0
    lateinit var user_gender: TextView //= null// :String= ""
    lateinit var user_mail: TextView //= null //:String= "" //view?.findViewById(R.id.)
    lateinit var user_number: TextView //= null//:Int =0
    lateinit var user_description: TextView //= null//:String= ""
    lateinit var user_languages: TextView //= null//:String = ""
    lateinit var user_city: TextView //= null//:String= ""
    //var user_time:String= ""
    var user_games: TextView? = null//:Array<String> = arrayOf("")
    //var user_feedback:Array<String> = arrayOf("") //view?.findViewById(R.id.)

    private lateinit var context : Context
    private lateinit var jsonObject : JSONObject
    private lateinit var gender : Spinner
    private lateinit var sportSpinner: Spinner
    private lateinit var sportLevelSpinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("HiltApplication", this.applicationContext.toString())
        context = this.applicationContext
        //getDataFromSharedPref()

        setContentView(R.layout.activity_show_profile)

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

        sharedPreference =  getSharedPreferences("preferences", 0) // 0 - for private mode
        this.user_image=findViewById(R.id.user_image)
        this.user_name=findViewById(R.id.user_name)
        this.user_nickname=findViewById(R.id.user_nickname)
        this.user_age=findViewById(R.id.user_age)
        this.user_gender=findViewById(R.id.user_gender)
        this.user_mail=findViewById(R.id.user_email)
        this.user_number=findViewById(R.id.user_number)
        this.user_languages=findViewById(R.id.user_languages)
        this.user_description=findViewById(R.id.user_description)
        this.user_city =findViewById(R.id.user_city)

        // TEST VIEWMODEL RESERVATIONS
        /* Activity/Fragment observes viewModel
        resViewModel.reservations.observe(this){
            if(it != null) {
                user_name.setText(it.toString())
            }
        }

        resViewModel.insertReservation(Reservation(3, 5, 1,"Lingotto","Basket",Date() ,"non so giocare",true))
        resViewModel.reservations.observe(this){
            if(it != null)
                user_nickname.setText(it.toString())
            else
                user_nickname.setText("")
        }

        resViewModel.singleReservation.observe(this) {
            if(it != null) {
                user_description.setText(it.toString())
            }
        }

        resViewModel.getSingleReservation(3)

         */

        // TEST VIEWMODEL POSRES
        /*
        posResViewModel.posRes.observe(this) {
            if (it != null) {
                user_name.setText(it.toString())
            }
        }

        posResViewModel.insertPosRes(PosRes(1, "Lingotto", 1, "Basket", Date(), true))

         */

        loadImageFromStorage()
        checkSharedPreference()
    }

    //handle toolbar items
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkSharedPreference() {
        val profile = sharedPreference.getString("profile", "").toString()

        if(profile!= ""){
            jsonObject = JSONObject(profile)
            println("IL MIO JESONOBJECT $jsonObject")
            var strName = jsonObject.getString(getString(R.string.save_username))
            user_name.setText(strName)

            val age = jsonObject.getString(getString(R.string.save_age))
            user_age.setText(age)

            strName = jsonObject.getString(getString(R.string.save_description))
            user_description.setText(strName)

           // strName = jsonObject.getString(getString(R.string.save_nickname))
           // user_nickname.setText(strName)

            strName = jsonObject.getString(getString(R.string.save_gender))
            user_gender.setText(strName)
            /*for (i in 0 until gender.adapter.count) {
                if (gender.getItemAtPosition(i).toString() == strName) {
                    gender.setSelection(i)
                }
            }*/

            strName = jsonObject.getString(getString(R.string.save_email))
            user_mail.setText(strName)

            val numTelephone = jsonObject.getString(getString(R.string.save_telnumber))
            user_number.setText(numTelephone.toString())


            if(jsonObject.has(getString(R.string.save_city))){
                strName = jsonObject.getString(getString(R.string.save_city))
                user_city.setText(strName)
            }

            if(jsonObject.has(getString(R.string.save_languages))){
                val savedLanguages = jsonObject.getString(getString(R.string.save_languages))
                user_languages.text = savedLanguages
            }
/*
            if(jsonObject.has(getString(R.string.save_numbersports))){
                val test = findViewById<LinearLayout>(R.id.sportsContainer)

                val num = jsonObject.getInt(getString(R.string.save_numbersports))

                val sportsGames = jsonObject.getString(getString(R.string.save_namesports)).replace("[", "").replace("]","").split(", ")
                val levelsGames = jsonObject.getString(getString(R.string.save_levelsports)).replace("[", "").replace("]","").split(", ")

                for(l in 0 until  num) {
                    val sportList = layoutInflater.inflate(R.layout.add_new_sport, test, false)

                    sportSpinner = sportList.findViewById(R.id.editGames)

                    ArrayAdapter.createFromResource(
                        this,
                        R.array.allsports,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        sportSpinner.adapter = adapter
                    }


                    for (j in 0 until sportSpinner.adapter.count) {
                        if (sportSpinner.getItemAtPosition(j).toString() == sportsGames[l]) {
                            sportSpinner.setSelection(j)
                            break
                        }
                    }

                    sportLevelSpinner = sportList.findViewById(R.id.editGameLevel)

                    ArrayAdapter.createFromResource(
                        this,
                        R.array.levels,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        sportLevelSpinner.adapter = adapter
                    }

                    for (j in 0 until sportLevelSpinner.adapter.count) {
                        if (sportLevelSpinner.getItemAtPosition(j).toString()  == levelsGames[l]) {
                            sportLevelSpinner.setSelection(j)
                            break
                        }
                    }


                    test.addView(sportList)

                }
            }*/

        }

    }

    private fun loadImageFromStorage() {
        val files: Array<String> = context.fileList()

        if(files.contains(filename)){
            val b = context.openFileInput(filename).fd
            val c = BitmapFactory.decodeFileDescriptor(b).toDrawable(resources)
            user_image.setImageDrawable(c)
        } else {
            user_image.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.user_image))
        }

    }

    /*override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if (id==R.id.action_edit) {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        getDataFromSharedPref()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromSharedPref() {
        val x:String ?= sharedPreference.getString("user_image",null)
        if (x!=null){
            val encodedImage: String = sharedPreference.getString("user_image", "").toString()
            val b: ByteArray = Base64.getDecoder().decode(encodedImage)
            val bitmapImage = BitmapFactory.decodeByteArray(b, 0, b.size)

            user_image.setImageBitmap(bitmapImage)
        }
        else{
            user_image.setImageResource(R.drawable.user_image)
        }
       // user_name.text = sharedPreference.getString("user_name",getString(R.string.user_name))//?.text=sharedPreference.getString("user_name",R.string.user_name.toString())!!//view?.findViewById(R.id.)
       // user_nickname.text= sharedPreference.getString("user_nickname",getString(R.string.user_nickname))!!//view?.findViewById(R.id.)
        user_age.text = sharedPreference.getString("user_age",getString(R.string.user_age))//view?.findViewById(R.id.)
        user_gender.text= sharedPreference.getString("user_gender",getString(R.string.user_gender))!!//view?.findViewById(R.id.)
        user_mail.text= sharedPreference.getString("user_mail",getString(R.string.user_email))!!//view?.findViewById(R.id.)
        user_number.text= sharedPreference.getString("user_number",getString(R.string.user_number))//view?.findViewById(R.id.)
        user_languages.text=sharedPreference.getString("user_languages",getString(R.string.user_languages))!!
        user_description.text= sharedPreference.getString("user_description",getString(R.string.user_description))!!
        user_city.text= sharedPreference.getString("user_city",getString(R.string.user_city))!!//view?.findViewById(R.id.)

         // user_games= //view?.findViewById(R.id.)
    }

}