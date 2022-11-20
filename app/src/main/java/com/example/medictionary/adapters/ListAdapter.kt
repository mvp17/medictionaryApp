package com.example.medictionary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.medictionary.R
import com.example.medictionary.models.Model

class ListAdapter(var mCtx:Context, var resources:Int, var items:List<Model>):ArrayAdapter<Model>(mCtx, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater:LayoutInflater = LayoutInflater.from(mCtx)
        val view:View=layoutInflater.inflate(resources,null)
        val imageView:ImageView = view.findViewById(R.id.imageBox)
        val titleTextView:TextView = view.findViewById(R.id.nameTxt)
        val descriptionTextView:TextView = view.findViewById(R.id.desTxt)
        val mItem: Model = items[position]
        Glide.with(mCtx.applicationContext).load("https://pillbox.nlm.nih.gov/assets/pills/large/"+mItem.img+".jpg").placeholder(R.drawable.download).into(imageView)
        //imageView.setImageDrawable(mCtx.resources.getDrawable(mItem.img))
        titleTextView.text = mItem.name
        descriptionTextView.text = mItem.des

        return view
    }
}