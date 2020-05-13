package com.hypnotabac.client

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_signup.*

class ClientSignupActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "ClientSignupActivity"
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
                Toast.makeText(this@ClientSignupActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(
                    this@ClientSignupActivity,
                    "Please enter your first name",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(
                    this@ClientSignupActivity,
                    "Please enter your last name",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@ClientSignupActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(
                    this@ClientSignupActivity,
                    "Please confirm your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (password.length < 3 || password.length > 12) {
                Toast.makeText(
                    this@ClientSignupActivity,
                    "Please enter a password between 3 and 12 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            firebaseDatabase.getReference("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var isEmailRegistered = false
                        var randomID=""
                        var hypnoID=""

                        val hypnos = dataSnapshot.value as Map<*, *>
                        hypnos.forEach{h->
                            val hypno = h.value as Map<*, *>
                            if(hypno["clients"]!=null){
                                (hypno["clients"] as Map<*, *>).forEach{ c->
                                    val singleClient = c.value as Map<*, *>
                                    val singleEmail = singleClient["email"] as String
                                    if(singleEmail==email){
                                        isEmailRegistered = true
                                        randomID = c.key.toString()
                                        hypnoID = hypno["userID"] as String
                                        return@forEach
                                    }
                                }
                            }
                            if(isEmailRegistered){
                                return@forEach
                            }
                        }
                        if(!isEmailRegistered){
                            Toast.makeText(this@ClientSignupActivity, "Cette adresse email n'est pas associée à un compte d'hypnothérapeute.", Toast.LENGTH_SHORT)
                                .show()
                            return
                        } else {
                            if (password == confirmPwd) {
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(
                                        this@ClientSignupActivity
                                    ) { task ->
                                        if (task.isSuccessful) {
                                            val user = firebaseAuth.currentUser
                                            val profileUpdates =
                                                UserProfileChangeRequest.Builder()
                                                    .setDisplayName(email).build()
                                            user!!.updateProfile(profileUpdates)

                                            //Remove random ID
                                            firebaseDatabase.getReference("users")
                                                .child(hypnoID)
                                                .child("clients")
                                                .child(randomID)
                                                .removeValue()

                                            val dbCurrentUser =
                                                firebaseDatabase.getReference("users")
                                                    .child(hypnoID)
                                                    .child("clients")
                                                    .child(user.uid)
                                            dbCurrentUser.child("userID").setValue(user.uid)
                                            dbCurrentUser.child("email").setValue(email)
                                            dbCurrentUser.child("hypnoID").setValue(hypnoID)
                                            dbCurrentUser.child("firstName").setValue(firstName)
                                            dbCurrentUser.child("lastName").setValue(lastName)
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    ClientLoginActivity::class.java
                                                )
                                            )
                                            Toast.makeText(
                                                this@ClientSignupActivity,
                                                "Sign up complete",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@ClientSignupActivity,
                                                "Sign up failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this@ClientSignupActivity,
                                    "The confirmed password doesn't match the password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })
        })
    }
}

