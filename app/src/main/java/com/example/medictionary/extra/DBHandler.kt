package com.example.medictionary.extra

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.medictionary.models.AlarmModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.*


class DBHandler(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1){
    private val firebaseFirestore = Firebase.firestore

    companion object {
        private const val DATABASE_NAME = "PillBoxDatabase"
        private const val TABLE_NAME = "Alarm"
        private const val Alarm_ID = "Alarm_ID"
        private const val Time_taking_pill = "Time_taking_pill"
        private const val Name = "Name"
        private const val Status = "Status"
        private const val Pill_ID = "Pill_ID"
        private const val Total_daily_amount = "Total_daily_amount"
        private const val Treatment_length = "Treatment_length"
        private const val Hours_per_dose = "Hours_per_dose"
        private const val User_id = "User_id"
        private const val Last_Day_Of_Taking_Pill = "Last_Day_Of_Taking_Pill"
   }

    override fun onCreate(db: SQLiteDatabase?) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       //creating table with fields
       val createTable = ("CREATE TABLE  $TABLE_NAME ($Alarm_ID TEXT PRIMARY KEY ,$Time_taking_pill  TEXT,$Name TEXT, $Pill_ID TEXT,$Total_daily_amount INTEGER,$Treatment_length INTEGER,$Hours_per_dose INTEGER,$Status INTEGER,$User_id TEXT,$Last_Day_Of_Taking_Pill TEXT)")
       db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
       db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
       onCreate(db)
    }


    fun updateStatus(status : Int,id : String){
        val db = this.writableDatabase
        db!!.execSQL("UPDATE $TABLE_NAME SET $Status = $status WHERE $Alarm_ID =  '$id'")
        firebaseFirestore.collection("Alarms").document(id).update("status",status)
            .addOnCompleteListener { }
        if(status == 1) {
            val treatment = getAlarmByID(id)[0].treatmentLength.toInt()
            db.execSQL("UPDATE $TABLE_NAME SET $Last_Day_Of_Taking_Pill = '${getLastDay(treatment)}' WHERE $Alarm_ID =  '$id'")
        }
    }


    fun addAlarm(alarm_ID: String, time_taking_pill: String, name: String, pill_ID: String,
                total_daily_amount: Int, treatment_length: Int, hours_per_dose: Int, status: Int, userId: String){
       val db = this.writableDatabase
       val contentValues = ContentValues()
       contentValues.put(Alarm_ID, alarm_ID)
       contentValues.put(Time_taking_pill, time_taking_pill)
       contentValues.put(Name, name)
       contentValues.put(Pill_ID, pill_ID)
       contentValues.put(Total_daily_amount, total_daily_amount)
       contentValues.put(Treatment_length, treatment_length)
       contentValues.put(Hours_per_dose, hours_per_dose)
       contentValues.put(Status, status)
       contentValues.put(Last_Day_Of_Taking_Pill, getLastDay(treatment_length).toString())
       contentValues.put(User_id, userId)
       db.insert(TABLE_NAME, null, contentValues)

    }

    fun getAlarms(user_id: String): List<AlarmModel> {
        val alarmsList = mutableListOf<AlarmModel>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $User_id='$user_id' ORDER BY $Time_taking_pill"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                do {
                    val alarm = AlarmModel(cursor.getString(cursor.getColumnIndex(Alarm_ID)),
                        cursor.getString(cursor.getColumnIndex(Time_taking_pill)),
                        cursor.getString(cursor.getColumnIndex(Name)),
                        cursor.getString(cursor.getColumnIndex(Total_daily_amount)),
                        cursor.getString(cursor.getColumnIndex(Last_Day_Of_Taking_Pill)),
                        cursor.getString(cursor.getColumnIndex(Treatment_length)),
                        cursor.getString(cursor.getColumnIndex(Hours_per_dose)),
                        cursor.getInt(cursor.getColumnIndex(Status)))
                    alarmsList.add(alarm)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return alarmsList
    }


    private fun getAlarmByID(id : String): List<AlarmModel> {
        val alarmsList = mutableListOf<AlarmModel>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $Alarm_ID = '$id'"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val alarm = AlarmModel(cursor.getString(cursor.getColumnIndex(Alarm_ID)),
                        cursor.getString(cursor.getColumnIndex(Time_taking_pill)),
                        cursor.getString(cursor.getColumnIndex(Name)),
                        cursor.getString(cursor.getColumnIndex(Total_daily_amount)),
                        cursor.getString(cursor.getColumnIndex(Last_Day_Of_Taking_Pill)),
                        cursor.getString(cursor.getColumnIndex(Treatment_length)),
                        cursor.getString(cursor.getColumnIndex(Hours_per_dose)),
                        cursor.getInt(cursor.getColumnIndex(Status)))
                    alarmsList.add(alarm)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return alarmsList
    }


    private fun getLastDay(treatment_length: Int): Long {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, treatment_length)

        return calendar.time.time
    }


    fun deleteTitle(id:String) : Boolean
    {
        firebaseFirestore.collection("Alarms").document(id)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$Alarm_ID='$id'", null) > 0;

    }

    fun getActiveAlarms(): List<AlarmModel> {
        val alarmsList = mutableListOf<AlarmModel>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        val calendar = Calendar.getInstance().time.time
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val alarm = AlarmModel(cursor.getString(cursor.getColumnIndex(Alarm_ID)),
                        cursor.getString(cursor.getColumnIndex(Time_taking_pill)),
                        cursor.getString(cursor.getColumnIndex(Name)),
                        cursor.getString(cursor.getColumnIndex(Total_daily_amount)),
                        cursor.getString(cursor.getColumnIndex(Last_Day_Of_Taking_Pill)),
                        cursor.getString(cursor.getColumnIndex(Treatment_length)),
                        cursor.getString(cursor.getColumnIndex(Hours_per_dose)),
                        cursor.getInt(cursor.getColumnIndex(Status)))

                    if(alarm.lastDayOfTakingPill.toLong() > calendar && alarm.status == 1) {
                        alarmsList.add(alarm)
                    }
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return alarmsList
    }


    fun restoreAlarms(user_id: String){
        firebaseFirestore.collection("Alarms")
                .whereEqualTo("user_Id", user_id).addSnapshotListener { value, _ ->
                    for (document in value!!) {
                        try {
                            addAlarm(document.id, document.data["time_taking_pill"] as String,
                                document.data["name"] as String, document.data["pill_ID"] as String,
                                document.data["total_daily_amount"].toString().toInt() ,
                                document.data["treatment_length"].toString().toInt(),
                                document.data["hours_per_dose"].toString().toInt(),
                                document.data["status"].toString().toInt(),
                                document.data["user_Id"] as String)
                        }catch (ex: Exception){
                            Log.d(TAG, "$ex")
                        }
                        Log.d(TAG, "${document.id} => ${document.data["user_Id"]}")
                    }
                }
    }

}
