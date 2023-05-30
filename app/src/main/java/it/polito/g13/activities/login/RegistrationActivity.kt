package it.polito.g13.activities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import dagger.hilt.android.AndroidEntryPoint
import it.polito.g13.R
import it.polito.g13.activities.editprofile.ShowProfileActivity
import it.polito.g13.activities.editprofile.SportsActivity
import it.polito.g13.firebase_objects.ProfileUser
import org.json.JSONObject
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
var glist= mutableListOf<String>()
const val filename = "myPhoto"


@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    // TEST VIEWMODEL RESERVATIONS
    //private val resViewModel: ReservationsViewModel by viewModels()

    //TEST VIEWMODEL POSRES
    //private val posResViewModel by viewModels<PosResViewModel>()

    private val db = Firebase.firestore

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
    lateinit var user_number: EditText //= null//:Int =0
    lateinit var user_description: EditText //= null//:String= ""

    lateinit var user_city: EditText //= null//:String= "


    /*Data for storage */
    private lateinit var context : Context
    private lateinit var jsonObject : JSONObject
    private lateinit var globalBitmap: Bitmap
    private lateinit var editor: SharedPreferences.Editor
    var num_sports =0
//gender=spinner no edit view


    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(FirebaseAuth.getInstance().currentUser?.providerId != "google.com") {
                val userInformations = hashMapOf(
                    "name" to FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                    "email" to FirebaseAuth.getInstance().currentUser?.email.toString()

                )
                val userRef =
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .collection("infos")
                        .document(FirebaseAuth.getInstance().currentUser?.displayName.toString())
                userRef.set(userInformations)
            }
            FirebaseAuth.getInstance().signOut();
            finishAffinity()
        }
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        setContentView(R.layout.registration_profile)
        jsonObject = JSONObject()
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        imageView = findViewById(R.id.user_image)
        sharedPreference =  getSharedPreferences("preferences", 0)
        editor = sharedPreference.edit()

        user_name=findViewById(R.id.editFullName)
        user_nickname=findViewById(R.id.editNickname)
        user_age=findViewById(R.id.editAge)
        user_number=findViewById(R.id.editNumber)
        user_description=findViewById(R.id.editDescription)
        user_city = findViewById(R.id.editCity)

        //user_mail.isEnabled = false
        //set text navbar
        val navbarText = findViewById<TextView>(R.id.navbar_text)
        navbarText.text = "Create your profile"
        val textInputName = findViewById<TextInputLayout>(R.id.textInputName)
        val textInputNickname = findViewById<TextInputLayout>(R.id.textInputNickname)

        user_age.text.clear()
        val confirmButton =findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            var hasError = false
            val nameText = user_name.text.toString()
            val nickname = user_nickname.text.toString()

            if (TextUtils.isEmpty(nickname)) {
                textInputNickname.error = "Questo campo è obbligatorio"
                hasError = true
            }else{
                textInputNickname.error = null
            }

            if (TextUtils.isEmpty(nameText)) {
                textInputName.error = "Questo campo è obbligatorio"
                hasError = true
            } else {
                textInputName.error = null
            }

            if(!hasError){
                saveDataToPref()

                //persistData()
                //this.finish()
            }
        }

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



        //auto complete text view for city
        val cityInput = findViewById<AutoCompleteTextView>(R.id.editCity)

        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        cityInput.setAdapter(cityAdapter)

        cityInput.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cityAdapter.getItem(position).toString()
            // Toast.makeText(this, "Selected city: $selectedCity", Toast.LENGTH_SHORT).show()
        }

        /*
        //add a new sport
        val addSportTextContainer = findViewById<RelativeLayout>(R.id.addSportTextContainer)
        val addSportIcon = findViewById<FloatingActionButton>(R.id.addSportIcon)
        num_sports = 0
        addSportTextContainer.setOnClickListener {
            if(num_sports < sports.size){
                handleNewSport()
                num_sports++
            }

        }
        addSportIcon.setOnClickListener {
            if(num_sports < sports.size){
                handleNewSport()
                num_sports++
            }
        }

         */

        //multi selection menu for languages
        languagesView = findViewById(R.id.editLanguages)

        val selectedLanguages = BooleanArray(languages.size) { false }
        val listLanguages = mutableListOf<Int>()
        // if (savedLanguages!=null && savedLanguages!="")
        //   savedLanguages?.split(",")?.forEach { j:String -> listLanguages.add(languages.indexOf(j))
        // }

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
//spinner for gender
        genderSpinner= findViewById(R.id.editGender)

        genderSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        textInputNickname.isHintEnabled = false
        textInputName.isHintEnabled = false
    }

    private fun persistData() {


        if (imageUri != null) {
            context.openFileOutput(filename, MODE_PRIVATE).use {

                globalBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }


        /*
                val test = findViewById<LinearLayout>(R.id.sportsContainer)
                if(test.childCount > 0) {
                    jsonObject.put(getString(R.string.save_numbersports), test.childCount)
                    val listOfSports = mutableListOf<String>()
                    val listOfLevels = mutableListOf<String>()

                    for (i in 0 until test.childCount) {
                        val v = test.getChildAt(i)
                        val game = v.findViewById<Spinner>(R.id.editGames)
                        val level = v.findViewById<Spinner>(R.id.editGameLevel)
                        val description = v.findViewById<TextView>(R.id.editDescription)
                        Log.d("PROVASALVATAGGIO", "$level, $description, $game")
                        listOfSports.add(game.selectedItem.toString())
                        listOfLevels.add(level.selectedItem.toString())
                    }

                    jsonObject.put(getString(R.string.save_namesports), listOfSports)
                    jsonObject.put(getString(R.string.save_levelsports), listOfLevels)

                }

         */

        val t = Toast.makeText(this, "Confirmed", Toast.LENGTH_SHORT)
        t.show()

    }

    //save state when there is device rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the captured image URI to the savedInstanceState Bundle
        outState.putParcelable("imageUri", imageUri)
        outState.putString("savedLanguages", savedLanguages)

    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle","onResume invoked")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("lifecycle","onDestroy invoked")

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

        Log.d("POLITOSTRINGDEBUGGER", "Ripristino il messaggio")
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycle","onPause invoked")
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
            globalBitmap = Bitmap.createScaledBitmap(
                rotatedBitmap,
                imageView!!.width,
                imageView!!.height,
                false
            )
            imageView?.setImageBitmap(globalBitmap)
        }

        if (requestCode == resultLoadImage && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            val bitmap = uriToBitmap(imageUri!!)
            val rotatedBitmap = rotateBitmap(bitmap!!, getImageRotation(imageUri))

            globalBitmap = Bitmap.createScaledBitmap(
                rotatedBitmap,
                imageView!!.width,
                imageView!!.height,
                false
            )
            imageView?.setImageBitmap(globalBitmap)
        }
        Log.d("POLITOONACTIVITY", "Ottengo il risultato")

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

    /*
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
            num_sports--
        }
    }

     */

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
        if(sharedPreference.contains("user_name"))
            user_name.setText(sharedPreference.getString("user_name",getString(R.string.user_name))) //?.text=sharedPreference.getString("user_name",R.string.user_name.toString())!!//view?.findViewById(R.id.)
        //= sharedPreference.getString("user_name","ciao")             ----------------- .append per gli edit text
        if(sharedPreference.contains("user_nickname"))
            user_nickname.setText( sharedPreference.getString("user_nickname",getString(R.string.user_nickname)))//view?.findViewById(R.id.)
        if(sharedPreference.contains("user_age"))
            user_age.setText(sharedPreference.getString("user_age",getString(R.string.user_age)))//view?.findViewById(R.id.)

        val gender = sharedPreference.getString("user_gender", "Not specified")
        glist = genders.toMutableList()
        if (gender == "Male") {
            glist[0] = "Male"
            glist[1] = "Not specified"
        }
        if (gender == "Female") {
            glist[0] = "Female"
            glist[2] = "Not specified"
        }


        //view?.findViewById(R.id.)           sharedPreference.getString("user_gender",""=!!
//        user_mail.setText( sharedPreference.getString("user_mail",getString(R.string.user_email)))//view?.findViewById(R.id.)
        if(sharedPreference.contains("user_number"))
            user_number.setText( sharedPreference.getString("user_number",getString(R.string.user_number)))//view?.findViewById(R.id.)
        if(sharedPreference.contains("user_language")) {
            savedLanguages = sharedPreference.getString("user_languages", getString(R.string.user_languages))
            languagesView!!.text = savedLanguages
        }
        if(sharedPreference.contains("user_description"))
            user_description.setText( sharedPreference.getString("user_description",getString(R.string.user_description))!!)
        if(sharedPreference.contains("user_city"))
            user_city.setText( sharedPreference.getString("user_city",getString(R.string.user_city))!!)//view?.findViewById(R.id.)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDataToPref(){
        val y:String?=sharedPreference.getString("user_image",null)
        val x=sharedPreference.edit()
        x.clear()
        val baos = ByteArrayOutputStream()
        if (imageUri!=null){
            //uriToBitmap(imageUri!!)!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            globalBitmap= Bitmap.createScaledBitmap(
                uriToBitmap(imageUri!!)!!,
                imageView!!.width,
                imageView!!.height,
                false
            )
            globalBitmap = rotateBitmap(globalBitmap, getImageRotation(imageUri))
            globalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val compressImage: ByteArray = baos.toByteArray()
            val sEncodedImage: String = Base64.getEncoder().encodeToString(compressImage)//Base64.encodeToString(compressImage, Base64.DEFAULT)
            x.putString("user_image",sEncodedImage)


            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("images/profile/${Firebase.auth.currentUser?.email}.jpg")
            val uploadTask = storageRef.putBytes(compressImage)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Log.d("STORAGE", "Scrittura Storage Riuscita")
            }.addOnFailureListener { exception ->
                Log.d("STORAGE", "Scrittura Storage FALLITA ${exception}")

            }

        }
        else
            x.putString("user_image",y)


        val genderSpinner = findViewById<Spinner>(R.id.editGender)
        if (user_name.text.toString() != "") {
            x.putString("user_name", user_name.text.toString())
            jsonObject.put(getString(R.string.save_username), user_name.text.toString() )

        }
        if (user_nickname.text.toString()!="") {
            x.putString("user_nickname", user_nickname.text.toString())
            jsonObject.put(getString(R.string.save_nickname), user_nickname.text.toString() )

        }
        if(user_age.text.toString()!="") {
            x.putString("user_age", user_age.text.toString())
            jsonObject.put(getString(R.string.save_age), user_age.text.toString())

        }


        if(user_number.text.toString()!="") {
            x.putString("user_number", user_number.text.toString())
            jsonObject.put(getString(R.string.save_telnumber), user_number.text.toString())

        }

        if(user_description.text.toString()!="") {
            x.putString("user_description", user_description.text.toString())
            jsonObject.put(getString(R.string.save_description), user_description.text.toString() )

        }

        if(user_city.text.toString()!="") {
            x.putString("user_city", user_city.text.toString())
            jsonObject.put(getString(R.string.save_city), user_city.text.toString())

        }
        if(savedLanguages!=""){
            x.putString("user_languages",savedLanguages)
            jsonObject.put(getString(R.string.save_languages), languagesView!!.text)

        }

        if(genderSpinner.selectedItem != null) {
            x.putString("user_gender" , genderSpinner.selectedItem.toString())
            jsonObject.put(getString(R.string.save_gender), genderSpinner.selectedItem.toString() )
        }

        x.putString("profile", jsonObject.toString())

        x.apply()

        val myUser = ProfileUser(user_name.text.toString(),
            user_nickname.text.toString(),
            user_age.text.toString(),
            genderSpinner.selectedItem as String?,
            FirebaseAuth.getInstance().currentUser?.email!!,
            user_number.text.toString(),
            user_description.text.toString(),
            savedLanguages,
            user_city.text.toString(),
        )

        val intent = Intent(this, SportsActivity::class.java)
        intent.putExtra("myUser", myUser)
        startActivity(intent)

        // x.putString("user_name", if (user_name.text.length!=0){return user_name.text}; else{})
        //insertUserProfile(myUser)

    }

    /*
    private fun insertUserProfile(myuser: ProfileUser) {
        val user = db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("profile").document("info")
        user.set(myuser)
            .addOnCompleteListener {

                val intent = Intent(this, ShowProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                val t = Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT)
                t.show()
                finish()
            }
    }

     */




}

