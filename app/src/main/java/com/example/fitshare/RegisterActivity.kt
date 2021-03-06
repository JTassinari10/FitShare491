package com.example.fitshare


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import io.realm.mongodb.Credentials
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_login.*

import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import org.bson.Document

class RegisterActivity : AppCompatActivity() {
    private lateinit var Reg_Email: EditText
    private lateinit var Reg_Password: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Reg_Email = findViewById(R.id.txtReg_Email)
        Reg_Password = findViewById(R.id.txtReg_Password)


        /*
        ivLeft.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }*/

        btnRegister.setOnClickListener {
            Register()
        }
    }

    /*
    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }
    */

    private fun onLoginFailed(errorMsg: String) {
        Log.e("Login", errorMsg)
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }

    private fun validateCredentials(): Boolean = when {
        // zero-length usernames and passwords are not valid (or secure), so prevent users from creating accounts with those client-side.
        Reg_Email.text.toString().isEmpty() -> false
        Reg_Password.text.toString().isEmpty() -> false
        else -> true
    }

    private fun Register() {
        val Reg_Email = this.Reg_Email.text.toString()
        val Reg_Password = this.Reg_Password.text.toString()

        // register a user using the Realm App we created in the TaskTracker class
        fitApp.emailPassword.registerUserAsync(Reg_Email, Reg_Password) {
            // re-enable the buttons after user registration returns a result
            //createUserButton.isEnabled = true

            if (!it.isSuccess) {
                onLoginFailed("Could not register user. Password must be between 6 and 128 characters")
                Log.e("Register", "Error: ${it.error}")
                btnRegister.isEnabled = true
            } else {
                Toast.makeText(baseContext, "Successfully registered user.", Toast.LENGTH_LONG).show()
                Log.i("Register", "Successfully registered user.")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                }
            }
        }
    }
