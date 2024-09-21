package com.example.kundali// Import necessary packages
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.kundali.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val AUTOCOMPLETE_PLACE_BOY_REQUEST_CODE = 1
    var pickupLongitude = 0.0
    var pickupLatitude = 0.0
    var latLng: LatLng? = null
    var sourceLatLong: LatLng? = null

    // Declare a binding variable
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyA2bSrjroNJthADBBC0l53d08wbx34iZvc")
        }

        binding.name.hint = "Enter your full Name"

        binding.datePicker.setOnClickListener{
            openDatePickerDialog(binding.datePicker)
        }

        binding.timePicker.setOnClickListener{
            openTimePickerDialog(binding.timePicker)
        }

        binding.placePicker.setOnClickListener{
            placesAPiCall(AUTOCOMPLETE_PLACE_BOY_REQUEST_CODE)
        }




    }

    private fun openDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun openTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(selectedTime)
            },
            hour, minute, true // true for 24-hour format
        )
        timePickerDialog.show()
    }

    private fun placesAPiCall(requestCode: Int) {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, requestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_PLACE_BOY_REQUEST_CODE) {
            when (resultCode) {     //binding.etAmount.text = it.data!!.rate
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.i("TAG", "Place: " + place.name + ", " + place.id)
                    latLng = place.latLng
                    pickupLongitude = latLng!!.longitude
                    pickupLatitude = latLng!!.latitude


                    binding.placePicker.text = place.address
                    sourceLatLong = LatLng(pickupLatitude, pickupLongitude)
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("TAG", status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                }
            }
            return
        }
    }

}
