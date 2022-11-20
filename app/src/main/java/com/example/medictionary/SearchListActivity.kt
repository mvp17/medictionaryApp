package com.example.medictionary


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.medictionary.adapters.ListAdapter
import com.example.medictionary.interfaces.JsonPlaceholderApi
import com.example.medictionary.models.Medicine
import com.example.medictionary.models.Model
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*



class SearchListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchlist)
        val bundle = intent.extras
        val type = bundle?.getString("type")
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(" https://datadiscovery.nlm.nih.gov/").build()
        val jsonPlaceholderApi=retrofit.create(JsonPlaceholderApi::class.java)
        val listView = findViewById<ListView>(R.id.custom_Lis_tView)
        val list = mutableListOf<Model>()
        val idsList = mutableListOf<String>()
        if(type=="byName") {
            val name = bundle.getString("name")
            val namesarray= arrayListOf<String>(name.toString().capitalize(Locale.ROOT),
                                                name.toString().toUpperCase(Locale.ROOT),
                                                name.toString().toLowerCase(Locale.ROOT))
            for (name_i in namesarray){
                val myCall: Call<List<Medicine>> = jsonPlaceholderApi.getMedicinesByName(name_i)
                getRowsAPI(myCall,list,idsList,listView)
            }

        }
        else{

            val color = bundle?.getString("color")
            val shape = bundle?.getString("shape")
            val code = bundle?.getString("code")
            val myCall: Call<List<Medicine>> = jsonPlaceholderApi.getMedicinesByCahr(shape.toString(),
                                                                                     color.toString(), code.toString())
            getRowsAPI(myCall,list,idsList,listView)
        }

        listView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            val intent = Intent(this, PillInfoActivity::class.java).apply {
                putExtra("itemId", idsList[position])
                putExtra("email", email.toString())
                putExtra("provider", provider.toString())
        }
            startActivity(intent)


    }


    }
    private fun String.showAlert() {
        val builder = AlertDialog.Builder(this@SearchListActivity)
        builder.setTitle("Error")
        builder.setMessage(this)
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private  fun getRowsAPI(myCall: Call<List<Medicine>>, list:MutableList<Model>, idsList:MutableList<String>, listView:ListView){
        myCall.enqueue(object : Callback<List<Medicine>> {
            override fun onResponse(
                    call: Call<List<Medicine>>,
                    response: Response<List<Medicine>>
            ) {

                val medicines: List<Medicine> = response.body()!!
                for (med in medicines) {

                    if (med.has_image == "True")
                        list.add(Model(med.medicine_name, med.spl_strength, med.splimage))
                    else
                        list.add(Model(med.medicine_name, med.spl_strength, ""))
                    idsList.add(med.id)

                }
                listView.adapter = ListAdapter(this@SearchListActivity, R.layout.row, list)
                Handler(Looper.getMainLooper()).postDelayed({
                    if(list.size==0)
                        "No results".showAlert()
                }, 1000)

            }
            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                Toast.makeText(
                        this@SearchListActivity,
                        "Error: ${t.message.toString()}",
                        Toast.LENGTH_LONG
                ).show()
            }

        })
    }

}

