package com.hypnotabac.hypno

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_edit_client.*

class EditClientActivity : AppCompatActivity() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()
    var clientID = ""
    var clientEmail = ""
    val TAG: String = "EditClientActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_client)

        clientID = intent.getStringExtra("clientID")
        clientEmail = intent.getStringExtra("clientEmail")

        btnEditEmail.setOnClickListener{
            display_email.visibility = View.INVISIBLE
            btnEditEmail.visibility = View.INVISIBLE
            editEmail.visibility = View.VISIBLE
            btnEditEmailOk.visibility = View.VISIBLE
        }

        btnEditEmailOk.setOnClickListener{
            val newEmail: String = editEmail.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(newEmail)) {
                updateUI()
                return@setOnClickListener
            }
            if(clientID!=""){
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(clientID).child("email").setValue(newEmail)
            } else {
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val clientsMap = dataSnapshot.value!! as Map<*, *>
                            clientsMap.forEach{ c->
                                val singleClient = c.value as Map<*, *>
                                val singleEmail = singleClient["email"] as String
                                if(singleEmail==clientEmail){
                                    val key = c.key.toString()
                                    firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(key).child("email").setValue(newEmail)
                                    return@forEach
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(TAG,"Database Error", error.toException())
                        }
                    })
            }

            Toast.makeText(this, "Changes successful", Toast.LENGTH_SHORT).show()
            updateUI()
        }

        btnEditFirstName.setOnClickListener{
            display_first_name.visibility = View.INVISIBLE
            btnEditFirstName.visibility = View.INVISIBLE
            editFirstName.visibility = View.VISIBLE
            btnEditFirstNameOk.visibility = View.VISIBLE
        }

        btnEditFirstNameOk.setOnClickListener{
            val newFirstName: String = editFirstName.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(newFirstName)) {
                updateUI()
                return@setOnClickListener
            }
            if(clientID!=""){
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(clientID).child("firstName").setValue(newFirstName)
            } else {
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val clientsMap = dataSnapshot.value!! as Map<*, *>
                            clientsMap.forEach{ c->
                                val singleClient = c.value as Map<*, *>
                                val singleEmail = singleClient["email"] as String
                                if(singleEmail==clientEmail){
                                    val key = c.key.toString()
                                    firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(key).child("firstName").setValue(newFirstName)
                                    return@forEach
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(TAG,"Database Error", error.toException())
                        }
                    })
            }


            Toast.makeText(this, "Changes successful", Toast.LENGTH_SHORT).show()
            updateUI()
        }

        btnEditLastName.setOnClickListener{
            display_last_name.visibility = View.INVISIBLE
            btnEditLastName.visibility = View.INVISIBLE
            editLastName.visibility = View.VISIBLE
            btnEditLastNameOk.visibility = View.VISIBLE
        }

        btnEditLastNameOk.setOnClickListener{
            val newLastName: String = editLastName.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(newLastName)) {
                updateUI()
                return@setOnClickListener
            }
            if(clientID!=""){
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(clientID).child("lastName").setValue(newLastName)
            } else {
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val clientsMap = dataSnapshot.value!! as Map<*, *>
                            clientsMap.forEach{ c->
                                val singleClient = c.value as Map<*, *>
                                val singleEmail = singleClient["email"] as String
                                if(singleEmail==clientEmail){
                                    val key = c.key.toString()
                                    firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(key).child("lastName").setValue(newLastName)
                                    return@forEach
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(TAG,"Database Error", error.toException())
                        }
                    })
            }

            Toast.makeText(this, "Changes successful", Toast.LENGTH_SHORT).show()
            updateUI()
        }

        btnDone.setOnClickListener{
            ContextCompat.startActivity(
            this,
            Intent(this, HypnoMainActivity::class.java),
            null
        )
        }
        updateUI()
    }

    fun updateUI() {
        clientID = intent.getStringExtra("clientID")
        clientEmail = intent.getStringExtra("clientEmail")

        // Read from the database
        if(clientID!=""){
            firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(clientID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val dbClient = dataSnapshot.value!! as Map<*, *>
                        if(dbClient.containsKey("userID")){
                            display_email.text = dbClient["email"] as String
                            display_first_name.text = dbClient["firstName"] as String
                            display_last_name.text = dbClient["lastName"] as String
                        } else {
                            display_email.text = dbClient["email"] as String
                            display_first_name.text = getString(R.string.first_name_not_set)
                            display_last_name.text = getString(R.string.last_name_not_set)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w(TAG,"Database Error", error.toException())
                    }
                })
        } else {
            firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val clientsMap = dataSnapshot.value!! as Map<*, *>
                        clientsMap.forEach{ c->
                            val dbClient = c.value!! as Map<*, *>
                            if(dbClient["email"]==clientEmail){
                                display_email.text = dbClient["email"] as String
                                if(dbClient.containsKey("firstName")) display_first_name.text = dbClient["firstName"] as String
                                else display_first_name.text = getString(R.string.first_name_not_set)
                                if(dbClient.containsKey("lastName")) display_last_name.text = dbClient["lastName"] as String
                                else display_last_name.text = getString(R.string.last_name_not_set)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w(TAG,"Database Error", error.toException())
                    }
                })
        }

        display_email.visibility = View.VISIBLE
        btnEditEmail.visibility = View.VISIBLE
        editEmail.visibility = View.INVISIBLE
        btnEditEmailOk.visibility = View.INVISIBLE

        display_first_name.visibility = View.VISIBLE
        btnEditFirstName.visibility = View.VISIBLE
        editFirstName.visibility = View.INVISIBLE
        btnEditFirstNameOk.visibility = View.INVISIBLE

        display_last_name.visibility = View.VISIBLE
        btnEditLastName.visibility = View.VISIBLE
        editLastName.visibility = View.INVISIBLE
        btnEditLastNameOk.visibility = View.INVISIBLE
    }

    @Override
    override fun onBackPressed() {
        ContextCompat.startActivity(
            this,
            Intent(this, HypnoMainActivity::class.java),
            null
        )
    }
}
