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
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_login.*
import kotlin.reflect.typeOf

class ClientSignupActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "ClientSignupActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_login)

        login!!.setOnClickListener(View.OnClickListener {
            loading.visibility = View.VISIBLE
            val email = editEmail!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@ClientSignupActivity, "Veuillez entrer votre adresse email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            firebaseDatabase.getReference("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var isEmailRegistered = false
                        var randomID=""
                        var firstName=""
                        var lastName=""
                        var responses = listOf<String>()
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
                                        hypnoID = h.key.toString()
                                        firstName = singleClient["firstName"] as String
                                        lastName = singleClient["lastName"] as String
                                        responses = singleClient["responses"] as List<String>
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
                            val intent = intent
                            val emailLink = intent.data!!.toString()

                            // Confirm the link is a sign-in with email link.
                            if (firebaseAuth.isSignInWithEmailLink(emailLink)) {

                                // The client SDK will parse the code from the link for you.
                                firebaseAuth.signInWithEmailLink(email, emailLink)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "Successfully signed in with email link!")
                                            // You can access the new user via result.getUser()
                                            // Additional user info profile *not* available via:
                                            // result.getAdditionalUserInfo().getProfile() == null
                                            // You can check if the user is new or existing:
                                            // result.getAdditionalUserInfo().isNewUser()

                                            val user = firebaseAuth.currentUser
                                            val profileUpdates =
                                                UserProfileChangeRequest.Builder()
                                                    .setDisplayName(email).build()
                                            user!!.updateProfile(profileUpdates)

                                            firebaseDatabase.getReference("users").removeEventListener(this)
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
                                            dbCurrentUser.child("email").setValue(email)
                                            dbCurrentUser.child("hypnoID").setValue(hypnoID)
                                            dbCurrentUser.child("firstName").setValue(firstName)
                                            dbCurrentUser.child("lastName").setValue(lastName)
                                            responses.forEachIndexed {i, r ->
                                                dbCurrentUser.child("responses").child(i.toString()).setValue(r)
                                            }
                                            SaveSharedPreferences.setEmail(this@ClientSignupActivity, email)
                                            SaveSharedPreferences.setUserType(this@ClientSignupActivity, "client")
                                            SaveSharedPreferences.setUserID(this@ClientSignupActivity, firebaseAuth.uid)
                                            SaveSharedPreferences.setHypnoID(this@ClientSignupActivity, hypnoID)
                                            loading.visibility = View.GONE
                                            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
                                            Toast.makeText(
                                                this@ClientSignupActivity,
                                                "Sign up complete",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            loading.visibility = View.GONE
                                            Log.e(TAG, "Error signing in with email link", task.exception)
                                        }
                                    }
                            } else {
                                Toast.makeText(this@ClientSignupActivity, "Vous devez vous connecter à l'aide d'un lien email", Toast.LENGTH_SHORT).show()
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

