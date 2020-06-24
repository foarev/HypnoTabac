package com.hypnotabac

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.client.ClientMainActivity
import com.hypnotabac.hypno.HypnoMainActivity

class StartupActivity : AppCompatActivity() {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        if (SaveSharedPreferences.getUserType(this) == "hypno" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
        } else if (SaveSharedPreferences.getUserType(this) == "client" && SaveSharedPreferences.getEmail(this) != "") {
            firebaseDatabase.getReference("users").child(SaveSharedPreferences.getHypnoID(this)).child("clients").child(SaveSharedPreferences.getUserID(this))
                .addValueEventListener(object :
                ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.value!=null){
                            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
                        }
                        else{
                            SaveSharedPreferences.resetAll(this@StartupActivity)
                            startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                        }
                    }
                }
            )
        } else {
            startActivity(Intent(applicationContext, WelcomeActivity::class.java))
        }
    }
}