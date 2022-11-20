package com.example.medictionary.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.medictionary.HomeActivity
import com.example.medictionary.ProviderType
import com.example.medictionary.R
import com.example.medictionary.SurveyActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class AuthActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val googleSignIn = 100
    private  val callbackManager = CallbackManager.Factory.create()
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Analytics Event
        firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("message", "Firebase integration complete")
        firebaseAnalytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            checkingaccount(email, ProviderType.valueOf(provider))
        }
    }


    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun setup() {
        title = "Authentication"

        signUpButton.setOnClickListener {

                if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                    if(passwordEditText.text.toString().length>8 && isValidPassword(
                            passwordEditText.text.toString()
                        )){
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(
                                emailEditText.text.toString(),
                                passwordEditText.text.toString()
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    checkingaccount(
                                        it.result?.user?.email ?: "",
                                        ProviderType.BASIC
                                    )
                                } else {
                                    val errorMessage = "Cannot sign up that email and password"
                                    showAlert(errorMessage)
                                }
                            }
                    }
                    else
                        showAlert("The password should contains number, letter, capital letters and sign")
                } else
                    showAlert("All fields are required")


        }

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener{
                        if (it.isSuccessful){
                            checkingaccount(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            val errorMessage = "Cannot log in with that email and password"
                            showAlert(errorMessage)
                        }
                    }
            }
            else
                showAlert("All fields are required")
        }
        text_click_forgot_pwd.setOnClickListener {
            val forgotPwdIntent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(forgotPwdIntent)
        }
        googleButton.setOnClickListener {

            // Configuration

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, googleSignIn)
        }

        facebookButton.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {

                        result?.let {
                            val token = it.accessToken

                            val credential = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        checkingaccount(
                                            task.result?.user?.email
                                                ?: "", ProviderType.FACEBOOK
                                        )
                                    } else {
                                        val errorMessage = "Facebook sign in account incorrect."
                                        showAlert(errorMessage)
                                    }
                                }
                        }
                    }

                    override fun onCancel() {
                        LoginManager.getInstance().logOut()
                    }

                    override fun onError(error: FacebookException?) {
                        val errorMessage = "Facebook Exception"
                        showAlert(errorMessage)
                    }

                })
        }

    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    private fun checkingaccount(email: String, provider: ProviderType){
        db.collection("Users").document(email).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.data != null) {
                   showHome(email,provider)
                    Log.d("TAG", "show home data: " + task.result!!.data)
                } else {
                    showSurvey(email,provider)
                    Log.d("TAG", "show survey data: ")
                }
            } else {
                Log.d("TAG", "show survey ", task.exception)
            }
        }
    }

    private fun showSurvey(email: String, provider: ProviderType) {
        val surveyIntent = Intent(this, SurveyActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(surveyIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if (it.isSuccessful){
                            checkingaccount(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            val errorMessage = "Google sign in account incorrect."
                            showAlert(errorMessage)
                        }
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

    }
}
