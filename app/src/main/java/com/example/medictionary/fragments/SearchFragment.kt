package com.example.medictionary.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.medictionary.R
import com.example.medictionary.SearchListActivity


class SearchFragment : Fragment() {

    override fun onCreateView(

            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        val bundle = arguments
        val email = bundle!!.getString("email")
        val provider = bundle.getString("provider")
        try{
            val shapeSpinner= root.findViewById<View>(R.id.shapeSpinner) as Spinner
            val colorSpinner=root.findViewById<View>(R.id.colorSpinner) as Spinner
            val shapes= arrayListOf("CAPSULE", "DOUBLE CIRCLE", "CLOVER", "TRIANGLE",
                "FREEFORM", "SQUARE", "BULLET", "HEXAGON (6 SIDED)", "OCTAGON (8 SIDED)", "DIAMOND",
                "TRAPEZOID", "PENTAGON (5 SIDED)", "RECTANGLE", "TEAR", "OVAL", "ROUND")
            val colors= arrayListOf("PINK", "YELLOW", "ORANGE", "BROWN", "BLUE", "RED", "WHITE",
                "PURPLE", "GREEN", "GRAY", "BLACK", "TURQUOISE")
            val shapeAdapter= ArrayAdapter(
                    activity!!,
                    android.R.layout.simple_spinner_item,
                    shapes
            )
            shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shapeSpinner.adapter = shapeAdapter
            val colorAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, colors)
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            colorSpinner.adapter = colorAdapter
            val searchByNameBtn = root.findViewById<View>(R.id.searchNameBtn) as ImageButton
            val nameEt = root.findViewById<View>(R.id.nameEt) as EditText

            searchByNameBtn.setOnClickListener {
                if (nameEt.text.isNotEmpty() || nameEt.text.isNotBlank()) {
                    val intent = Intent(activity, SearchListActivity::class.java)
                    intent.putExtra("type", "byName")
                    intent.putExtra("name", nameEt.text.toString())
                    intent.putExtra("email", email.toString())
                    intent.putExtra("provider", provider.toString())
                    startActivity(intent)

                } else
                    showAlert("Name field should not be empty")
            }
            val searchByCharBtn = root.findViewById<View>(R.id.searchCharBtn) as Button
            val codeTxt = root.findViewById<View>(R.id.codeEt) as EditText

            searchByCharBtn.setOnClickListener {
                if (codeTxt.text.isNotEmpty() || codeTxt.text.isNotBlank()) {
                    val intent = Intent(activity, SearchListActivity::class.java)
                    intent.putExtra("type", "byChar")
                    intent.putExtra("color", colorSpinner.selectedItem.toString())
                    intent.putExtra("shape", shapeSpinner.selectedItem.toString())
                    intent.putExtra("code", codeTxt.text.toString())
                    intent.putExtra("email", email.toString())
                    intent.putExtra("provider", provider.toString())
                    startActivity(intent);
                }else
                    showAlert("Code field should not be empty")

            }

        }catch (ex: Exception){
            Toast.makeText(activity!!, "$ex", Toast.LENGTH_LONG).show()
        }

        return root
    }
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}