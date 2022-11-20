package com.example.medictionary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.medictionary.interfaces.JsonPlaceholderApi
import com.example.medictionary.models.Medicine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class PillInfoActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pillinfo)
        val bundle = intent.extras
        val id = bundle?.getString("itemId")
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val setAlarm = findViewById<View>(R.id.setAlarm)as Button
        val nameTxt = findViewById<View>(R.id.medicineName)as TextView
        val description = findViewById<View>(R.id.description)as TextView
        val imprint = findViewById<View>(R.id.imprint)as TextView
        val shape = findViewById<View>(R.id.shape)as TextView
        val color = findViewById<View>(R.id.color)as TextView
        val supplier = findViewById<View>(R.id.supplier)as TextView
        val size = findViewById<View>(R.id.size)as TextView
        val inactiveIngredients = findViewById<View>(R.id.inactiveIngredients)as TextView
        val strength = findViewById<View>(R.id.strength)as TextView
        val ingredients = findViewById<View>(R.id.ingredients)as TextView
        val imageView = findViewById<View>(R.id.person) as ImageView
        var medName=""

            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(" https://datadiscovery.nlm.nih.gov/").build()
            val jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi::class.java)
            val myCall: Call<List<Medicine>> = jsonPlaceholderApi.getMedicinesById(id.toString())

            myCall.enqueue(object : Callback<List<Medicine>> {
                override fun onResponse(call: Call<List<Medicine>>, response: Response<List<Medicine>>) {
                    val medicine = response.body()
                    if (medicine != null) {
                        for (details in medicine){
                            medName = details.medicine_name
                            nameTxt.text = details.medicine_name
                            Glide.with(this@PillInfoActivity).load("https://pillbox.nlm.nih.gov/assets/pills/large/"+details.splimage+".jpg")
                                .placeholder(R.drawable.download).into(imageView)

                            val content = "Pill is with imprint " + details.splimprint + ", "+details.splcolor_text.toLowerCase(
                                Locale.ROOT) + " color and " + details.splshape_text + " shape. It has been identified as " +
                                    details.rxstring + ". It is supplied by " + details.author + " corporation."

                            description.text = content
                            imprint.text = details.splimprint.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                            shape.text = details.splshape_text.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                            color.text = details.splcolor_text.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                            strength.text = details.spl_strength.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                            size.text = details.splsize.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)+" mm"
                            supplier.text = details.author.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)+" corporation"
                            ingredients.text = details.spl_ingredients.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                            inactiveIngredients.text = details.spl_inactive_ing.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                    Toast.makeText(this@PillInfoActivity, t.message.toString(), Toast.LENGTH_LONG).show()
                }

            })
            setAlarm.setOnClickListener {
                val intent = Intent(this, SetAlarmActivity::class.java).apply {
                    putExtra("medId", id.toString())
                    putExtra("medName", medName)
                    putExtra("email", email)
                    putExtra("provider", provider)

                }
                startActivity(intent)
            }
    }
}



