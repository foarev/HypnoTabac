package com.hypnotabac.hypno

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignUp!!.setOnClickListener(View.OnClickListener {
            val email = editEmail!!.text.toString().trim { it <= ' ' }
            val password = editPassword!!.text.toString().trim { it <= ' ' }
            val confirmPwd =
                confirmPassword!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@SignupActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@SignupActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(
                    this@SignupActivity,
                    "Please confirm your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (password.length < 3 || password.length > 12) {
                Toast.makeText(
                    this@SignupActivity,
                    "Please enter a password between 3 and 12 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (password == confirmPwd) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        this@SignupActivity
                    ) { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            val profileUpdates =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(email).build()
                            user!!.updateProfile(profileUpdates)
                            val dbCurrentUser =
                                firebaseDatabase.getReference("users")
                                    .child("hypnos")
                                    .child(user.uid)
                            dbCurrentUser.child("userID").setValue(user.uid)
                            dbCurrentUser.child("email").setValue(email)
                            startActivity(
                                Intent(
                                    applicationContext,
                                    HypnoLoginActivity::class.java
                                )
                            )
                            Toast.makeText(
                                this@SignupActivity,
                                "Sign up complete",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                "Sign up failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this@SignupActivity,
                    "The confirmed password doesn't match the password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
        })
    }
}

