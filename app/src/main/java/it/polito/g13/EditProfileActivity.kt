package it.polito.g13

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
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

    private var savedLanguages: String? = ""
    private var languagesView: TextView? = null
    //imageView for profile pic
    private var imageView: ImageView? = null
    //variable used to take picture from camera
    private var imageUri: Uri? = null
    private val resultLoadImage = 123
    private val imageCaptureCode = 654

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //change profile picture
        imageView = findViewById(R.id.user_image)
        val imgButton = findViewById<ImageButton>(R.id.imageButton)
        registerForContextMenu(imgButton)

        //auto complete text view for city
        val cityInput = findViewById<AutoCompleteTextView>(R.id.editCity)

        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        cityInput.setAdapter(cityAdapter)

        cityInput.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cityAdapter.getItem(position).toString()
            Toast.makeText(this, "Selected city: $selectedCity", Toast.LENGTH_SHORT).show()
        }

        //spinner for gender
        val genderSpinner = findViewById<Spinner>(R.id.editGender)
        genderSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        //spinner for sport
        val sportSpinner = findViewById<Spinner>(R.id.editGames)
        sportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sports)

        //spinner for sport level
        val sportLevelSpinner = findViewById<Spinner>(R.id.editGameLevel)
        sportLevelSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sportLevels)

        //time picker for start time
        val startTimeView = findViewById<EditText>(R.id.editStartTime)

        startTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                startTimeView.setText(selectedTime)
            }, hour, minutes, true)

            timePickerDialog.show()
        }

        //time picker for end time
        val endTimeView = findViewById<EditText>(R.id.editEndTime)

        endTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                endTimeView.setText(selectedTime)
            }, hour, minutes, true)

            timePickerDialog.show()
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

    //menu to edit profile pic (take picture or select from gallery)
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_img_menu, menu)
    }

    //option selected to edit profile picture
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_from_gallery -> {
                //choose image from gallery
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                @Suppress("DEPRECATION")
                startActivityForResult(galleryIntent, resultLoadImage)
                true
            }
            R.id.take_picture -> {
                //ask for permission of camera upon first launch of application
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, 112)
                }
                else {
                    openCamera()
                }
                true
            }
            else -> super.onContextItemSelected(item)
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
}