package com.example.medictionary.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medictionary.*
import com.example.medictionary.adapters.AlarmListAdapter
import com.example.medictionary.extra.DBHandler
import com.example.medictionary.services.ServiceTrigger
import com.example.medictionary.interfaces.CellClickListener
import com.example.medictionary.models.AlarmModel
import kotlinx.android.synthetic.main.alarmrow.view.*
import kotlinx.android.synthetic.main.fragment_pill_box.*
import java.lang.Exception
import java.util.*


class PillBoxFragment : Fragment(), CellClickListener {
    lateinit var dbHelper:DBHandler
    private val channelId = "pills"
    var list = mutableListOf<AlarmModel>()
    lateinit var adpter : AlarmListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pill_box, container, false)
    }
    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val format : String = if(android.text.format.DateFormat.is24HourFormat(activity)){
            "24"
        } else { "12" }
            val bundle = arguments
            val email = bundle!!.getString("email").toString()
            // val provider = bundle.getString("provider")
            val intent = Intent(activity!!, ServiceTrigger::class.java)
            activity!!.startService(intent)
            alarmrecylerView.apply {
                  try {
                      dbHelper = DBHandler(activity!!)
                      list =
                          dbHelper.getAlarms(email) as MutableList<AlarmModel>
                      if (list.size == 0) {
                          showAlert("No results")
                      }
                      adpter = AlarmListAdapter(this@PillBoxFragment, list, format)
                      alarmrecylerView.apply {
                          layoutManager = LinearLayoutManager(activity!!)
                          adapter = adpter
                      }
                      createNotificationChannel()
                      val calendar = Calendar.getInstance()
                      for (i: AlarmModel in list) {
                          if (i.lastDayOfTakingPill.toLong() < calendar.time.time) {
                              dbHelper.updateStatus(0, i.id)
                          }
                      }
                  }catch (ex:Exception){
                      showAlert(ex.toString())
                  }
        }

    }
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCellClickListener(it: View, id:String) {
        // val statusnum=1
        dbHelper.updateStatus(
            status = if (it.statueSwitch.isChecked) {
                1
            } else {
                0
            }, id = id
        )


    }

     override fun onCellDeleteListener(it: View,id:String,position: Int) {
        try {
            dbHelper.deleteTitle(id)
            list.removeAt(position)
            adpter.notifyItemRemoved(position)
        }catch (ex:Exception){
            Toast.makeText(activity!!,"$ex",Toast.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = channelId
            val description = "pills"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                channelId, name,
                importance
            )
            val idAudio = R.raw.ringtone
            channel.description = description
            channel.enableVibration(true)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(Uri.parse("android.resource://${activity!!.packageName}/$idAudio"), audioAttributes)
            val notificationManager = activity!!.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(activity!!, ServiceTrigger::class.java)
        activity!!.startService(intent)
    }


}
