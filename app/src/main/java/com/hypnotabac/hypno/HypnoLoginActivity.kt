package com.hypnotabac.hypno

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 0
    private val TAG = "LoginActivity"
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        login!!.setOnClickListener(View.OnClickListener {
            val email = username!!.text.toString().trim { it <= ' ' }
            val password = password!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@LoginActivity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            val displayName = firebaseAuth!!.currentUser!!.displayName
                            SaveSharedPreferences.setUserName(
                                this@LoginActivity,
                                displayName
                            )
                            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
                            Toast.makeText(
                                this@LoginActivity,
                                "Successfully logged in as $displayName",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Wrong email address or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        })

        signup.setOnClickListener{
            startActivity(Intent(applicationContext, SignupActivity::class.java))
        }
    }
}