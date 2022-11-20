package com.example.medictionary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medictionary.R
import com.example.medictionary.interfaces.CellClickListener
import com.example.medictionary.models.AlarmModel
import kotlinx.android.synthetic.main.alarmrow.view.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmListAdapter(
    private val cellClickListener: CellClickListener,
    private val alarms: MutableList<AlarmModel>,
    private val format: String):RecyclerView.Adapter<AlarmListAdapter.ViewHolder>() {

    private val calendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    var sdf12Hour: SimpleDateFormat = SimpleDateFormat("hh:mm a")

    @SuppressLint("SimpleDateFormat")
    var sdf24Hour = SimpleDateFormat("HH:mm")

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alarmNameTxt:TextView=itemView.alarm_nameTxt
        val timeTxt:TextView=itemView.timeTxt
        val statueSwitch = itemView.statueSwitch!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.alarmrow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.alarmNameTxt.text=alarms[position].name
        if(format == "12"){
            val dt24Hour = sdf24Hour.parse(alarms[position].time)
            holder.timeTxt.text = sdf12Hour.format(dt24Hour).toString()
        }else {
            holder.timeTxt.text=alarms[position].time
        }

        holder.statueSwitch.isChecked = (alarms[position].status == 1) &&
                (alarms[position].lastDayOfTakingPill.toLong() > calendar.time.time)
        holder.itemView.statueSwitch.setOnCheckedChangeListener { _, _ -> cellClickListener.onCellClickListener(
            holder.itemView,alarms[position].id
        ) }
        holder.itemView.deleteButton.setOnClickListener {
            cellClickListener.onCellDeleteListener(holder.itemView,alarms[position].id, holder.adapterPosition)
        }
    }

    override fun getItemCount()=alarms.size


}