package com.hypnotabac.client

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_h_login.*


class ClientLoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 0
    private val TAG = "ClientLoginActivity"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_login)

        login!!.setOnClickListener(View.OnClickListener {
            val email = username!!.text.toString().trim { it <= ' ' }
            val password = password!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@ClientLoginActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@ClientLoginActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@ClientLoginActivity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            SaveSharedPreferences.setEmail(this@ClientLoginActivity, email)
                            SaveSharedPreferences.setUserType(this@ClientLoginActivity, "client")
                            SaveSharedPreferences.setUserID(this@ClientLoginActivity, firebaseAuth.uid)

                            firebaseDatabase.getReference("users")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        Log.w(TAG, "Failed to read value.", p0.toException())
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val hypnos = dataSnapshot.value as Map<*, *>
                                        hypnos.forEach{h->
                                            val hypno = h.value as Map<*, *>
                                            if(hypno["clients"]!=null){
                                                val m = (hypno["clients"] as Map<*, *>)
                                                if(m.containsKey(firebaseAuth.uid)){
                                                    SaveSharedPreferences.setHypnoID(this@ClientLoginActivity, h.key.toString())
                                                    startActivity(Intent(applicationContext, ClientMainActivity::class.java))
                                                }
                                            }
                                        }
                                    }
                                })
                        } else {
                            Toast.makeText(
                                this@ClientLoginActivity,
                                "Wrong email address or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        })
        signup.setOnClickListener{
            startActivity(Intent(applicationContext, ClientSignupActivity::class.java))
        }
    }
}