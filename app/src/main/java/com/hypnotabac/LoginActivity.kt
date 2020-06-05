package com.hypnotabac

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.hypnotabac.client.ClientMainActivity
import com.hypnotabac.hypno.HypnoMainActivity
import com.hypnotabac.hypno.HypnoSignupActivity
import kotlinx.android.synthetic.main.activity_h_login.*

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 0
    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_login)
        if (SaveSharedPreferences.getUserType(this) == "hypno" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
        } else if (SaveSharedPreferences.getUserType(this) == "client" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
        } else {
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
                                SaveSharedPreferences.setEmail(this@LoginActivity, email)
                                SaveSharedPreferences.setUserType(this@LoginActivity, "hypno")
                                SaveSharedPreferences.setUserID(this@LoginActivity, firebaseAuth!!.uid)
                                startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
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
                startActivity(Intent(applicationContext, HypnoSignupActivity::class.java))
            }
        }
    }
}