package it.polito.g13

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import kotlinx.serialization.encodeToString
import java.io.ByteArrayOutputStream
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

val cities = listOf(
    "Agrigento", "Alessandria", "Ancona", "Aosta", "Arezzo", "Ascoli Piceno", "Asti", "Avellino", "Bari", "Barletta-Andria-Trani", "Belluno", "Benevento",
    "Bergamo", "Biella", "Bologna", "Bolzano", "Brescia", "Brindisi", "Cagliari", "Caltanissetta", "Campobasso", "Carbonia-Iglesias", "Caserta", "Catania",
    "Catanzaro", "Chieti", "Como", "Cosenza", "Cremona", "Crotone", "Cuneo", "Enna", "Fermo", "Ferrara", "Firenze", "Foggia", "Forlì-Cesena", "Frosinone",
    "Genova", "Gorizia", "Grosseto", "Imperia", "Isernia", "La Spezia", "L'Aquila", "Latina", "Lecce", "Lecco", "Livorno", "Lodi", "Lucca", "Macerata",
    "Mantova", "Massa-Carrara", "Matera", "Medio Campidano", "Messina", "Milano", "Modena", "Monza e della Brianza", "Napoli", "Novara", "Nuoro", "Ogliastra",
    "Olbia-Tempio", "Oristano", "Padova", "Palermo", "Parma", "Pavia", "Perugia", "Pesaro e Urbino", "Pescara", "Piacenza", "Pisa", "Pistoia", "Pordenone",
    "Potenza", "Prato", "Ragusa", "Ravenna", "Reggio Calabria", "Reggio Emilia", "Rieti", "Rimini", "Roma", "Rovigo", "Salerno", "Sassari", "Savona", "Siena",
    "Siracusa", "Sondrio", "Taranto", "Teramo", "Terni", "Torino", "Trapani", "Trento", "Treviso", "Trieste", "Udine", "Varese", "Venezia", "Verbano-Cusio-Ossola",
    "Vercelli", "Verona", "Vibo Valentia", "Vicenza", "Viterbo"
)
val genders = listOf("Not specified", "Male", "Female")
val sports = listOf("Basket", "Football", "Padel", "Rugby", "Tennis", "Volleyball")
val sportLevels = listOf("Beginner", "Intermediate", "Professional")
val languages = arrayOf("English", "Italian", "French", "German", "Spanish", "Arabic", "Chinese")

class EditProfileActivity : AppCompatActivity() {

    lateinit var sharedPreference:SharedPreferences
    //selected languages saved after device rotation
    private var savedLanguages: String? = ""
    private var languagesView: TextView? = null
    //imageView for profile pic
    private var imageView: ImageView? = null
    lateinit var genderSpinner : Spinner
    //variable used to take picture from camera
    private var imageUri: Uri? = null
    private val resultLoadImage = 123
    private val imageCaptureCode = 654
    lateinit var user_name: EditText //= null//: String= ""
    lateinit var user_nickname: EditText //= null//:String= ""
    lateinit var user_age: EditText //= null//:Int =0
    lateinit var user_gender: EditText //= null// :String= ""
    lateinit var user_mail: EditText //= null //:String= "" //view?.findViewById(R.id.)
    lateinit var user_number: EditText //= null//:Int =0
    lateinit var user_description: EditText //= null//:String= ""

    lateinit var user_city: EditText //= null//:String= "

//gender=spinner no edit view

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.user_image)
        sharedPreference =  getSharedPreferences("preferences", 0);
        this.user_name=findViewById(R.id.editFullName)
        this.user_nickname=findViewById(R.id.editNickname)
        this.user_age=findViewById(R.id.editAge)
        this.user_mail=findViewById(R.id.editEmail)
        this.user_number=findViewById(R.id.editNumber)
        this.user_description=findViewById(R.id.editDescription)
        this.user_city =findViewById(R.id.editCity)
        getDataFromSharedPref()

        val confirmButton =findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener{
            saveDataToPref()
            this.finish()
        }
        val cancelButton =findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener { this.finish() }

        //change profile picture

        val imgButton = findViewById<ImageButton>(R.id.imageButton)

        //menu to edit profile pic (take picture or select from gallery)
        imgButton.setOnClickListener {
            val popup = PopupMenu(this, imgButton)
            popup.menuInflater.inflate(R.menu.edit_img_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                //handle context menu item click
                handlePictureChange(item)
            }
            popup.show()
        }

        //spinner for gender
        genderSpinner= findViewById(R.id.editGender)
        genderSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        //auto complete text view for city
        val cityInput = findViewById<AutoCompleteTextView>(R.id.editCity)

        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        cityInput.setAdapter(cityAdapter)

        cityInput.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cityAdapter.getItem(position).toString()
            Toast.makeText(this, "Selected city: $selectedCity", Toast.LENGTH_SHORT).show()
        }

        //add a new sport
        val addSportTextContainer = findViewById<RelativeLayout>(R.id.addSportTextContainer)
        val addSportIcon = findViewById<FloatingActionButton>(R.id.addSportIcon)

        addSportTextContainer.setOnClickListener {
            handleNewSport()
        }
        addSportIcon.setOnClickListener {
            handleNewSport()
        }

        //multi selection menu for languages
        languagesView = findViewById(R.id.editLanguages)
        val selectedLanguages = BooleanArray(languages.size) { false }
        val listLanguages = mutableListOf<Int>()

        languagesView!!.setOnClickListener {
            //initialize alert dialog
            val builder = AlertDialog.Builder(this)

            //set title
            builder.setTitle("Select languages")

            //set dialog non cancelable
            builder.setCancelable(false)

            builder.setMultiChoiceItems(
                languages,
                selectedLanguages
            ) { _: DialogInterface, i: Int, b: Boolean ->
                //check condition
                if (b) {
                    //when checkbox selected add position in lang list
                    listLanguages.add(i)
                    //sort array list
                    listLanguages.sort()
                } else {
                    //when checkbox unselected remove position from listLanguages
                    listLanguages.remove(i)
                }
            }

            builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
                //initialize string builder
                val stringBuilder = StringBuilder()
                //use for loop
                for (j in listLanguages.indices) {
                    //concat array value
                    stringBuilder.append(languages[listLanguages[j]])
                    //check condition
                    if (j != listLanguages.size - 1) {
                        //when j value not equal to lang list size - 1, add comma
                        stringBuilder.append(", ")
                    }
                }
                //set text on textView
                savedLanguages = stringBuilder.toString()
                languagesView!!.text = savedLanguages
            }

            builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                //dismiss dialog
                dialogInterface.dismiss()
            }
            builder.setNeutralButton("Clear All") { _: DialogInterface, _: Int ->
                //use for loop
                for (j in selectedLanguages.indices) {
                    //remove all selection
                    selectedLanguages[j] = false
                    //clear language list
                    listLanguages.clear()
                    //clear text view value
                    languagesView!!.text = ""
                }
            }
            //show dialog
            builder.show()
        }
    }

    //save state when there is device rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the captured image URI to the savedInstanceState Bundle
        outState.putParcelable("imageUri", imageUri)
        outState.putString("savedLanguages", savedLanguages)
    }

    //restore state when device is rotated
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the captured image URI from the savedInstanceState Bundle
        @Suppress("DEPRECATION")
        imageUri = savedInstanceState.getParcelable("imageUri")
        imageView?.setImageURI(imageUri)

        savedLanguages = savedInstanceState.getString("savedLanguages")
        languagesView?.text = savedLanguages
    }

    //option selected to edit profile picture
    private fun handlePictureChange(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_from_gallery -> {
                //android 13
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                    requestPermissions(permission, 112)
                }
                //lower version
                else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, 113)
                }
                else {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    @Suppress("DEPRECATION")
                    startActivityForResult(galleryIntent, resultLoadImage)
                }
                true
            }
            R.id.take_picture -> {
                //ask for permission of camera
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.CAMERA)
                    requestPermissions(permission, 114)
                }
                else {
                    openCamera()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    //manage permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 112 || requestCode == 113) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //choose image from gallery
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                @Suppress("DEPRECATION")
                startActivityForResult(galleryIntent, resultLoadImage)
            }
        }
        else if (requestCode == 114) {
            openCamera()
        }
    }


    //open camera so that user can capture image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        @Suppress("DEPRECATION")
        startActivityForResult(cameraIntent, imageCaptureCode)
    }

    //manage result for picture taken from camera or selected from gallery
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageCaptureCode && resultCode == Activity.RESULT_OK) {
            val bitmap = uriToBitmap(imageUri!!)
            val rotatedBitmap = rotateBitmap(bitmap!!, getImageRotation(imageUri))
            imageView?.setImageBitmap(rotatedBitmap)
        }

        if (requestCode == resultLoadImage && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            val bitmap = uriToBitmap(imageUri!!)
            imageView?.setImageBitmap(bitmap)
        }
    }

    //takes URI of the image captured and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //fixing rotated picture problem
    private fun rotateBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @SuppressLint("Recycle")
    private fun getImageRotation(imageUri: Uri?): Int {
        val inputStream = contentResolver.openInputStream(imageUri!!)
        val exif = ExifInterface(inputStream!!)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
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

        //delete inserted sport
        val deleteSportIcon = sportList.findViewById<FloatingActionButton>(R.id.delete_sport)
        deleteSportIcon.setOnClickListener {
            addSportContainer.removeView(sportList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromSharedPref(){
        val x:String ?= sharedPreference.getString("user_image",null)
        if (x!=null){
            val encodedImage: String? = sharedPreference.getString("user_image", null)
            val b: ByteArray = Base64.getDecoder().decode(encodedImage)
            val bitmapImage = BitmapFactory.decodeByteArray(b, 0, b.size)
            imageView?.setImageBitmap(bitmapImage)
        }
        else{
            imageView?.setImageResource(R.drawable.user_image)
        }
        user_name.setText(sharedPreference.getString("user_name",getString(R.string.user_name))) //?.text=sharedPreference.getString("user_name",R.string.user_name.toString())!!//view?.findViewById(R.id.)
         //= sharedPreference.getString("user_name","ciao")             ----------------- .append per gli edit text
        user_nickname.setText( sharedPreference.getString("user_nickname",getString(R.string.user_nickname))!!)//view?.findViewById(R.id.)
        user_age.setText(sharedPreference.getString("user_age",getString(R.string.user_age)))//view?.findViewById(R.id.)
       // user_gender.setSelection(genders.indexOf("Male")) //view?.findViewById(R.id.)           sharedPreference.getString("user_gender",""=!!
        user_mail.setText( sharedPreference.getString("user_mail",getString(R.string.user_email))!!)//view?.findViewById(R.id.)
        user_number.setText( sharedPreference.getString("user_number",getString(R.string.user_number)))//view?.findViewById(R.id.)
        //user_languages.hint=sharedPreference.getString("user_nickname",getString(R.string.user_languages))!!
        user_description.setText( sharedPreference.getString("user_description",getString(R.string.user_description))!!)
        user_city.setText( sharedPreference.getString("user_city",getString(R.string.user_city))!!)//view?.findViewById(R.id.)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDataToPref(){
        var y:String?=sharedPreference.getString("user_image",null)
        var x=sharedPreference.edit()
        x.clear()
        val baos = ByteArrayOutputStream()
        if (imageUri!=null){
            uriToBitmap(imageUri!!)!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val compressImage: ByteArray = baos.toByteArray()
            val sEncodedImage: String = Base64.getEncoder().encodeToString(compressImage)//Base64.encodeToString(compressImage, Base64.DEFAULT)
            x.putString("user_image",sEncodedImage)
        }
        else
            x.putString("user_image",y)


        val genderSpinner = findViewById<Spinner>(R.id.editGender)
        if (user_name.text.toString()!="")
            x.putString("user_name",user_name.text.toString())
        if (user_nickname.text.toString()!="")
            x.putString("user_nickname",user_nickname.text.toString())
        if(user_age.text.toString()!="")
            x.putString("user_age",user_age.text.toString())
        if(user_mail.text.toString()!="")
            x.putString("user_mail",user_mail.text.toString())
        if(user_number.text.toString()!="")
            x.putString("user_number",user_number.text.toString())
        if(user_description.text.toString()!="")
            x.putString("user_description",user_description.text.toString())
        if(user_city.text.toString()!="")
            x.putString("user_city",user_city.text.toString())
        if(savedLanguages!="")
            x.putString("user_languages",savedLanguages)
        x.putString("user_gender" , genderSpinner.selectedItem.toString())
        x.apply()

       // x.putString("user_name", if (user_name.text.length!=0){return user_name.text}; else{})


    }


}