package com.hypnotabac.client

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
                            val displayName = firebaseAuth.currentUser!!.displayName
                            SaveSharedPreferences.setEmail(
                                this@ClientLoginActivity,
                                displayName
                            )
                            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
                            Toast.makeText(
                                this@ClientLoginActivity,
                                "Successfully logged in as $displayName",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@ClientLoginActivity,
                                "Wrong email address or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        })
    }
}