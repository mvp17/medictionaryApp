package com.example.medictionary.models


class AlarmModel(val id:String,
                 val time:String,
                 val name:String,
                 val totalDailyAmount:String,
                 val lastDayOfTakingPill:String,
                 val treatmentLength:String,
                 val hoursPerDose:String,
                 val status:Int) {
}
