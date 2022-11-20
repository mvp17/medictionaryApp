package com.example.medictionary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.medictionary.models.SurveyModel
import com.example.medictionary.models.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_survey.*

class SurveyActivity: AppCompatActivity() {
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val drinker= findViewById<View>(R.id.drinkSpinner) as Spinner
        val smoker= findViewById<View>(R.id.smokeSpinner) as Spinner
        val breakfast= findViewById<View>(R.id.breakfastSpinner) as Spinner
        val lunch= findViewById<View>(R.id.lunchSpinner) as Spinner
        val coldMd= findViewById<View>(R.id.coldMdSpinner) as Spinner
        val prescribed= findViewById<View>(R.id.prescribedSpinner) as Spinner
        val allergy=findViewById<View>(R.id.allergySpinner) as Spinner
        val spinner= arrayListOf("Yes","No")
        val spinnerAdapter= ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinner
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        drinker.adapter=spinnerAdapter
        smoker.adapter=spinnerAdapter
        breakfast.adapter=spinnerAdapter
        lunch.adapter=spinnerAdapter
        coldMd.adapter=spinnerAdapter
        prescribed.adapter=spinnerAdapter
        allergy.adapter=spinnerAdapter

        srvBtn.setOnClickListener {
            val survey = SurveyModel( drinker.selectedItem.toString(),smoker.selectedItem.toString(),breakfast.selectedItem.toString(),lunch.selectedItem.toString(),coldMd.selectedItem.toString(),prescribed.selectedItem.toString(),allergy.selectedItem.toString(),email.toString())
            val user = UserModel(email.toString(), provider.toString())
            db.collection("Users")
                .document(email.toString()).set(user)
                .addOnSuccessListener { _ ->
                    Log.d("DocSnippets", "DocumentSnapshot written")
                }
                .addOnFailureListener { e ->
                    Log.w("DocSnippets", "Error adding document", e)
                }
            db.collection("Survey")
                .document(email.toString()).set(survey)
                .addOnSuccessListener { _ ->
                    Log.d("DocSnippets", "DocumentSnapshot written")
                }
                .addOnFailureListener { e ->
                    Log.w("DocSnippets", "Error adding document", e)
                }
            val surveyIntent = Intent(this, HomeActivity::class.java).apply {
              putExtra("email", email)
               putExtra("provider", provider)
               }
            startActivity(surveyIntent)
        }
    }
}