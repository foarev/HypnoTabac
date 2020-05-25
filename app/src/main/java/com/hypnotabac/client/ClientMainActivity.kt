package com.hypnotabac.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.FirstLoginActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_main.*


class ClientMainActivity : AppCompatActivity() {
    val TAG = "ClientMainActivity"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_main)

        firebaseDatabase.getReference("users").child(SaveSharedPreferences.getHypnoID(this)).child("clients").child(firebaseAuth.currentUser!!.uid).child("firstName")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value!=null){
                        textView.text = "Welcome "+dataSnapshot.value as String
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })

        logout.setOnClickListener{
            SaveSharedPreferences.resetAll(this)
            startActivity(Intent(applicationContext, FirstLoginActivity::class.java))
        }

    }
}
