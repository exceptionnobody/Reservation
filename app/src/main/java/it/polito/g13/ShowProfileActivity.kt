package it.polito.g13

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class ShowProfileActivity : AppCompatActivity() {
    //val sharedPreference =  getSharedPreferences("preferences",Context.MODE_PRIVATE); // 0 - for private mode
    var user_name: String= ""
    var user_nickname:String= ""
    var user_age:Int =0
    var user_gender:String= ""
    var user_mail:String= "" //view?.findViewById(R.id.)
    var user_number:Int =0
    var user_description:String= ""
    var user_languages:Array<String> = arrayOf("")
    var user_city:String= ""
    //var user_time:String= ""
    var user_games:Array<String> = arrayOf("")
    //var user_feedback:Array<String> = arrayOf("") //view?.findViewById(R.id.)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
    }override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        /*   if (file.exists()){
         try {
         user_name= view?.findViewById(R.id.)
          user_nickname= view?.findViewById(R.id.)
          user_age = view?.findViewById(R.id.)
          user_gender= view?.findViewById(R.id.)
          user_mail= view?.findViewById(R.id.)
          user_number= view?.findViewById(R.id.)
          user_description= view?.findViewById(R.id.)
          user_languages= view?.findViewById(R.id.)
          user_city= view?.findViewById(R.id.)
          user_time= view?.findViewById(R.id.)
          user_games= view?.findViewById(R.id.)
          user_feedback= view?.findViewById(R.id.)

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
          else{
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
          }
    */  }

}