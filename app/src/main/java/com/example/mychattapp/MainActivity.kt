package com.example.mychattapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerBtn = findViewById<Button>(R.id.register_button)
        registerBtn.setOnClickListener(View.OnClickListener {
            performRegister()
        })

        val haveAccountTV = findViewById<TextView>(R.id.have_account_textview)

        haveAccountTV.setOnClickListener(View.OnClickListener {
            Log.d("MainActivity", "show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })

        val select_photo = findViewById<Button>(R.id.image_button_register)
        select_photo.setOnClickListener(View.OnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            startActivityForResult(intent, 0)
        })

    }

    var selectedPhotoUri: Uri? = null

    //takes image in register activity and saves it displays it in a CircleImageView above
    //select_image_button_register (button still available)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("register", "photo was selected")
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val select_photo_change_background = findViewById<CircleImageView>(R.id.setimage_imageview_register)
            select_photo_change_background.setImageBitmap(bitmap)
            val select_image_button_register = findViewById<Button>(R.id.image_button_register)
            select_image_button_register.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //val select_photo_change_background = findViewById<Button>(R.id.image_button_register)
            //select_photo_change_background.setBackgroundDrawable(bitmapDrawable)
        }
    }

    //method in (Register) Activity that takes the input from the page and creates a user
    //with createUserWithEmailAndPassword using Firebase
    //method also uploads image to storage using uploadImageToFirebaseStorage method
    private fun performRegister() {
        val email = findViewById<EditText>(R.id.email_edittext_register).text.toString()
        val username = findViewById<EditText>(R.id.username_edittext_register).text.toString()
        val password = findViewById<EditText>(R.id.password_edittext_register).text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("MainActivity", "Email is: $email")
        Log.d("MainActivity", "Password is: $password")

        //firebase implementation
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

                //else
                Log.d("Main", "Successfully created user with uid: " + it.result.user)

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to register", Toast.LENGTH_LONG).show()
            }
    }


    //saves the image to Firebase Storage
    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("register", "successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    saveUserToDatabase(it.toString())
                }
            }
    }

    //saves user to database and changes intent to LatestMessagesActivity
    private fun saveUserToDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = findViewById<EditText>(R.id.username_edittext_register).text.toString()
        val email = findViewById<EditText>(R.id.email_edittext_register).text.toString()
        val password = findViewById<EditText>(R.id.password_edittext_register).text.toString()
        val user = User(uid, username, profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("register", "user saved to firebase")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }
}
/*
class User(val uid: String, val username: String, val profileImageUrl: String){
    constructor() : this ("", "", "")
}
*/
