package com.bea.gitscope

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bea.gitscope.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var button: Button

//    private val correctMail = "test@gmail.com"
//    private val correctPassword = "123"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.mailInput)
        editTextPassword = findViewById(R.id.passwordInput)
        button = findViewById(R.id.button)
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()

        button.setOnClickListener {

            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Email",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

            //logga in knapp
//        binding.button.setOnClickListener {
//            Firebase.analytics
//
//            val email = binding.mailInput.text.toString().trim()
//            val password = binding.passwordInput.text.toString()
//
//            if (email == correctMail && password == correctPassword) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Wrong mail or password",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }

        }

        //register link till register vy
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    //kolla om användaren redan är inloggad
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}