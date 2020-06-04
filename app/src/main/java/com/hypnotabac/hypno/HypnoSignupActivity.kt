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
import com.hypnotabac.LoginActivity
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_signup.*

class HypnoSignupActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "HypnoSignupActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignUp!!.setOnClickListener(View.OnClickListener {
            val email = editEmail!!.text.toString().trim { it <= ' ' }
            val firstName = editFirstName!!.text.toString().trim { it <= ' ' }
            val lastName = editLastName!!.text.toString().trim { it <= ' ' }
            val password = editPassword!!.text.toString().trim { it <= ' ' }
            val confirmPwd = confirmPassword!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@HypnoSignupActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "Please enter your first name",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "Please enter your last name",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "Please confirm your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (password.length < 3 || password.length > 12) {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "Please enter a password between 3 and 12 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (password == confirmPwd) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        this@HypnoSignupActivity
                    ) { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            val profileUpdates =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(email).build()
                            user!!.updateProfile(profileUpdates)
                            val dbCurrentUser =
                                firebaseDatabase.getReference("users")
                                    .child(user.uid)
                            dbCurrentUser.child("email").setValue(email)
                            dbCurrentUser.child("firstName").setValue(firstName)
                            dbCurrentUser.child("lastName").setValue(lastName)
                            startActivity(
                                Intent(
                                    applicationContext,
                                    LoginActivity::class.java
                                )
                            )
                            Toast.makeText(
                                this@HypnoSignupActivity,
                                "Sign up complete",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@HypnoSignupActivity,
                                "Sign up failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this@HypnoSignupActivity,
                    "The confirmed password doesn't match the password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
        })
    }
}

