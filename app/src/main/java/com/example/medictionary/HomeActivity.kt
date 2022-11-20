package com.example.medictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.medictionary.extra.DBHandler
import com.example.medictionary.fragments.PillBoxFragment
import com.example.medictionary.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_home.*


enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    private val searchFragment=SearchFragment()
    private val pillBoxFragment=PillBoxFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val alarm = bundle?.getString("alarm")
        val dbHelper = DBHandler(this)
        dbHelper.restoreAlarms(email.toString())

        if (alarm.toString() == "alarm")
            replaceFragment(pillBoxFragment, email.toString(), provider.toString())
        else
            replaceFragment(searchFragment, email.toString(), provider.toString())
        bottm_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_search -> replaceFragment(searchFragment, email.toString(), provider.toString())
                R.id.ic_pillbox -> replaceFragment(pillBoxFragment, email.toString(), provider.toString())
            }
            true
        }

        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }
    private fun replaceFragment(fragment: Fragment,email:String,provider:String){
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("provider", provider)
        fragment.arguments = bundle;
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}