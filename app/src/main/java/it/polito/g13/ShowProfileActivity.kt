package it.polito.g13



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toDrawable
import java.util.*

class ShowProfileActivity : AppCompatActivity() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        //getDataFromSharedPref()
        setContentView(R.layout.activity_show_profile)
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

        loadImageFromStorage()
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
        user_name.text = sharedPreference.getString("user_name",getString(R.string.user_name))//?.text=sharedPreference.getString("user_name",R.string.user_name.toString())!!//view?.findViewById(R.id.)
        user_nickname.text= sharedPreference.getString("user_nickname",getString(R.string.user_nickname))!!//view?.findViewById(R.id.)
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