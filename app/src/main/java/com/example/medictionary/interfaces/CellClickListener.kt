package com.example.medictionary.interfaces

import android.view.View

interface CellClickListener {
    fun onCellClickListener(it: View,id:String)
    fun onCellDeleteListener(it: View,id:String,position:Int)
}