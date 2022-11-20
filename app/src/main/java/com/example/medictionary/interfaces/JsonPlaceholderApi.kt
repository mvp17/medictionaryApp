package com.example.medictionary.interfaces

import com.example.medictionary.models.Medicine
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface JsonPlaceholderApi {
    @GET("resource/crzr-uvwg.json")
    fun getMedicinesByCahr(@Query("splshape_text") splshape_text: String,
                           @Query("splcolor_text") splcolor_text: String,
                           @Query("splimprint") splimprint: String):Call<List<Medicine>>
    @GET("resource/crzr-uvwg.json")
    fun getMedicinesByName(@Query("medicine_name") medicine_name:String) :Call<List<Medicine>>
    @GET("resource/crzr-uvwg.json")
    fun getMedicinesById(@Query("id") id:String) :Call<List<Medicine>>

}