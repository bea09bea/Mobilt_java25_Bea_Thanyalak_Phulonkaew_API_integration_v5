package com.bea.gitscope

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextName : EditText
    private lateinit var createBtn: Button
    private lateinit var auth: FirebaseAuth

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextName = findViewById(R.id.editTextName)
        createBtn = findViewById(R.id.createBtn)

        createBtn.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString()
            val name = editTextName.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Email",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            } else if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->

                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "User created!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(Intent(this, MainActivity::class.java)
                                    )

                                    finish()
                                }
                            }

                    } else {
                        Log.w(
                            "RegisterActivity",
                            "createUserWithEmail:failure",
                            task.exception
                        )

                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Registration failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        //Register link -> login activity
        val link = findViewById<TextView?>(R.id.loginLink)
        link.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(i)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}

