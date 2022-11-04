package com.example.mychattapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<Button>(R.id.login_button_login)
        loginBtn.setOnClickListener(View.OnClickListener {
            val email = findViewById<EditText>(R.id.email_edittext_login).text.toString()
            val password = findViewById<EditText>(R.id.password_edittext_login).text.toString()

            Log.d("LoginActivity", "attempt to login with email and password $email")

            //attempting to log in user with already existing user info and redirecting to LatestMessageActivity
            //if it fails returns toast
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (!it.isSuccessful) return@addOnCompleteListener

                    //else
                    Log.d("logged", "logged in successfully" + it.result)

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("logged", "failed to login user" + it.message)
                }

        })
        val backToLogin = findViewById<TextView>(R.id.back_to_registration_textview_login)
        backToLogin.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

}