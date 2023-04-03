package it.polito.g13



import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.*


import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Array.getInt


class ShowProfileActivity : AppCompatActivity() {
    lateinit var sharedPreference:SharedPreferences
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //getDataFromSharedPref()
        setContentView(R.layout.activity_show_profile)
        sharedPreference =  getSharedPreferences("preferences", 0) // 0 - for private mode
        this.user_name=findViewById(R.id.user_name)
        this.user_nickname=findViewById(R.id.user_nickname)
        this.user_age=findViewById(R.id.user_age)
        this.user_gender=findViewById(R.id.user_gender)
        this.user_mail=findViewById(R.id.user_email)
        this.user_number=findViewById(R.id.user_number)
        this.user_languages=findViewById(R.id.user_languages)
        this.user_description=findViewById(R.id.user_description)
        this.user_city =findViewById(R.id.user_city)
        user_name.text="ciao"
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

    override fun onResume() {
        super.onResume()
        getDataFromSharedPref()
    }

    private fun getDataFromSharedPref() {
        user_name.text = sharedPreference.getString("user_name",getString(R.string.user_name))//?.text=sharedPreference.getString("user_name",R.string.user_name.toString())!!//view?.findViewById(R.id.)
        user_nickname.text= sharedPreference.getString("user_nickname",getString(R.string.user_nickname))!!//view?.findViewById(R.id.)
        user_age.text = sharedPreference.getString("user_age",getString(R.string.user_age))//view?.findViewById(R.id.)
        user_gender.text= sharedPreference.getString("user_gender",getString(R.string.user_gender))!!//view?.findViewById(R.id.)
        user_mail.text= sharedPreference.getString("user_mail",getString(R.string.user_email))!!//view?.findViewById(R.id.)
        user_number.text= sharedPreference.getString("user_number",getString(R.string.user_number))//view?.findViewById(R.id.)
        user_languages.text=sharedPreference.getString("user_nickname",getString(R.string.user_languages))!!
        user_description.text= sharedPreference.getString("user_description",getString(R.string.user_description))!!
        user_city.text= sharedPreference.getString("user_city",getString(R.string.user_city))!!//view?.findViewById(R.id.)

         // user_games= //view?.findViewById(R.id.)

/*
          var file= File((context as Context).filesDir,fileName)
          if (file.exists()){
              try {
                  val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
                  val jsonObject = JSONObject(inputAsString)
                  fullName?.text =
                      jsonObject.getString(resources.getString(R.string.saved_fullName_key))
                  nickname?.text =
                      jsonObject.getString(resources.getString(R.string.saved_nickname_key))
                  location?.text =
                      jsonObject.getString(resources.getString(R.string.saved_location_key))
                  skill1?.text = jsonObject.getString(resources.getString(R.string.saved_skill1_key))
                  subskill1?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subskill1_key))
                  subsubskill1?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subsubskill1_key))
                  skill2?.text = jsonObject.getString(resources.getString(R.string.saved_skill2_key))
                  subskill2?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subskill2_key))
                  subsubskill2?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subsubskill2_key))
                  skill3?.text = jsonObject.getString(resources.getString(R.string.saved_skill3_key))
                  subskill3?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subskill3_key))
                  subsubskill3?.text =
                      jsonObject.getString(resources.getString(R.string.saved_subsubskill3_key))
                  desc?.text = jsonObject.getString(resources.getString(R.string.saved_desc_key))
                  email?.text = jsonObject.getString(resources.getString(R.string.saved_email_key))
                  phone?.text = jsonObject.getString(resources.getString(R.string.saved_phone_key))
                  try {
                      cameraFilePath =
                          jsonObject.getString(resources.getString(R.string.saved_photo_key))
                      photoChoice =
                          jsonObject.getString(resources.getString(R.string.saved_photochoice_key))
                      if (cameraFilePath != void) {
                          if (photoChoice == "Camera") {
                              img?.setImageBitmap(BitmapFactory.decodeFile(cameraFilePath))
                          }
                          if (photoChoice == "Gallery") {
                              img?.setImageURI(Uri.parse("$cameraFilePath"))
                          }
                      }
                  } catch (e: Exception) {
                      cameraFilePath=void
                      photoChoice="camera"
                  }
              }catch(e:Exception){}
          }

          //if file does not exist

              fullName?.text= (resources.getString(R.string.saved_default_fullName))
              nickname?.text= (resources.getString(R.string.saved_default_nickname))
              location?.text = (resources.getString(R.string.saved_default_location))
              skill1?.text = (resources.getString(R.string.saved_default_skill1))
              subskill1?.text = (resources.getString(R.string.saved_default_subskill1))
              subsubskill1?.text = (resources.getString(R.string.saved_default_subsubskill1))
              skill2?.text = (resources.getString(R.string.saved_default_skill2))
              subskill2?.text = (resources.getString(R.string.saved_default_subskill2))
              subsubskill2?.text = (resources.getString(R.string.saved_default_subsubskill2))
              skill3?.text = (resources.getString(R.string.saved_default_skill3))
              subskill3?.text = (resources.getString(R.string.saved_default_subskill3))
              subsubskill3?.text = (resources.getString(R.string.saved_default_subsubskill3))
              desc?.text = (resources.getString(R.string.saved_default_desc))
              email?.text = (resources.getString(R.string.saved_default_email))
              phone?.text = (resources.getString(R.string.saved_default_phone))
              cameraFilePath=void
              photoChoice="Camera"

    */  }

}