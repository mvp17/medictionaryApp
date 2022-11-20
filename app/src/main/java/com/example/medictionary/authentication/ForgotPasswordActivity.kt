package com.example.medictionary.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.medictionary.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setUp()
    }

    override fun onStart() {
        super.onStart()
        fgtPwdLayout.visibility = View.VISIBLE
    }

    private fun setUp () {
        val firebaseAuth = FirebaseAuth.getInstance()

        btnForgotPassword.setOnClickListener {
            firebaseAuth.sendPasswordResetEmail(userEmail.text.toString())
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this,
                            "Password sent to your email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this,
                            it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}