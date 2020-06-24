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
import com.hypnotabac.hypno.ForgotPasswordActivity
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
        firebaseAuth = FirebaseAuth.getInstance()
        login!!.setOnClickListener(View.OnClickListener {
            loading.visibility = View.VISIBLE
            val email = username!!.text.toString().trim { it <= ' ' }
            val password = password!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Veuillez entrer un email", Toast.LENGTH_SHORT)
                    .show()
                loading.visibility = View.GONE
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Veuillez entrer un mot de passe",
                    Toast.LENGTH_SHORT
                ).show()
                loading.visibility = View.GONE
                return@OnClickListener
            }
            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@LoginActivity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            SaveSharedPreferences.resetAll(this@LoginActivity)
                            SaveSharedPreferences.setEmail(this@LoginActivity, email)
                            SaveSharedPreferences.setUserType(this@LoginActivity, "hypno")
                            SaveSharedPreferences.setUserID(this@LoginActivity, firebaseAuth!!.uid)
                            loading.visibility = View.GONE
                            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
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