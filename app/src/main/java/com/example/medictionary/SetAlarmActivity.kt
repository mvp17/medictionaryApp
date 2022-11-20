package com.example.medictionary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.medictionary.extra.DBHandler
import com.example.medictionary.services.ServiceTrigger
import com.example.medictionary.models.ALarmModelFB
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.android.synthetic.main.activity_setalarm.*
import java.lang.Exception
import java.util.*

class SetAlarmActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var saveBtn: Button
    private lateinit var days: EditText
    private lateinit var number: EditText
    private lateinit var hours: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var dbHelper: DBHandler
    private val status = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setalarm)
        val bundle = intent.extras
        val id = bundle?.getString("medId")
        val name = bundle?.getString("medName")
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        saveBtn = findViewById<View>(R.id.setAlarmBtn) as Button
        days = findViewById<View>(R.id.days) as EditText
        number = findViewById<View>(R.id.number) as EditText
        hours = findViewById<View>(R.id.hours) as EditText
        timePicker = findViewById<View>(R.id.timepicker) as TimePicker
        val format : String = if(android.text.format.DateFormat.is24HourFormat(this)){
            "24"
        } else { "12" }
        if (format=="24")
            timePicker.setIs24HourView(true)
        dbHelper = DBHandler(this)

        saveBtn.setOnClickListener {
            try {
                val datetime = formatTimes(timePicker.hour.toString()) + ":" + formatTimes(timePicker.minute.toString())
                showAlert("Would you like to save this alarm?", datetime, name.toString(),
                           email.toString(),id.toString(),provider.toString())

            } catch (ex: Exception) {
                ex.printStackTrace()
                Toast.makeText(applicationContext, "$ex", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatTimes(time: String): String {
        return if (time.length < 2) {
            "0$time"
        } else {
            time
        }
    }

    private fun generateTimes(element: String): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val times = arrayListOf(element)
        calendar.set(Calendar.HOUR_OF_DAY, element.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, element.split(":")[1].toInt())
        for (i in 1 until number.text.toString().toInt()) {
            calendar.add(Calendar.HOUR, hours.text.toString().toInt())
            times.add(formatTimes(calendar.time.hours.toString()) + ":" + formatTimes(calendar.time.minutes.toString()))
        }
        return times
    }

    private fun showAlert(message: String, element: String,name:String, email: String, id: String,provider:String) {
       try {
           val builder = AlertDialog.Builder(this)
           builder.setTitle("Save")
           builder.setMessage(message + "\n${generateTimes(element)}\nFor ${days.text} days")
           builder.setPositiveButton("Accept") { _, _ ->
               val alarm = ALarmModelFB(
                   element,
                   name,
                   id,
                   number.text.toString().toInt(),
                   days.text.toString().toInt(),
                   hours.text.toString().toInt(),
                   status,
                   email
               )
               db.collection("Alarms").add(alarm)
                   .addOnSuccessListener { documentReference ->
                       dbHelper.addAlarm(
                           documentReference.id,
                           element,
                           name,
                           id,
                           number.text.toString().toInt(),
                           days.text.toString().toInt(),
                           hours.text.toString().toInt(),
                           status,
                           email
                       )
                   }
               val intent = Intent(this,HomeActivity::class.java)
               intent.putExtra("email", email)
               intent.putExtra("provider", provider)
               intent.putExtra("alarm", "alarm")
               startActivity(intent)
           }
           builder.setNegativeButton("edit", null)
           val dialog: AlertDialog = builder.create()
           dialog.show()
       }catch (ex:Exception){
           Toast.makeText(this@SetAlarmActivity,"$ex", Toast.LENGTH_LONG).show()
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, ServiceTrigger::class.java)
        startService(intent)
    }
}

