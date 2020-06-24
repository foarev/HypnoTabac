package com.hypnotabac.hypno

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_h_login.*

class HypnoLoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 0
    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_login)
        firebaseAuth = FirebaseAuth.getInstance()
        login!!.setOnClickListener(View.OnClickListener {
            loading.visibility = View.VISIBLE
            val email = username!!.text.toString().trim { it <= ' ' }
            val password = password!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@HypnoLoginActivity, "Veuillez entrer un email", Toast.LENGTH_SHORT)
                    .show()
                loading.visibility = View.GONE
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@HypnoLoginActivity,
                    "Veuillez entrer un mot de passe",
                    Toast.LENGTH_SHORT
                ).show()
                loading.visibility = View.GONE
                return@OnClickListener
            }
            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@HypnoLoginActivity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            SaveSharedPreferences.resetAll(this@HypnoLoginActivity)
                            SaveSharedPreferences.setEmail(
                                this@HypnoLoginActivity,
                                email
                            )
                            SaveSharedPreferences.setUserType(
                                this@HypnoLoginActivity,
                                "hypno"
                            )
                            SaveSharedPreferences.setUserID(
                                this@HypnoLoginActivity,
                                firebaseAuth!!.uid
                            )
                            loading.visibility = View.GONE
                            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
                        } else {
                            Toast.makeText(
                                this@HypnoLoginActivity,
                                "L'adresse email ou le mot de passe est incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                            loading.visibility = View.GONE
                        }
                    })
        })

        signup.setOnClickListener{
            startActivity(Intent(applicationContext, HypnoSignupActivity::class.java))
        }
        forgotPassword.setOnClickListener{
            startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
        }
    }
}