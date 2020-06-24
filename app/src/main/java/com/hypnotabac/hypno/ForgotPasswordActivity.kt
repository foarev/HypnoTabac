package com.hypnotabac.hypno

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.hypnotabac.LoginActivity
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        send.setOnClickListener{
            loading.visibility = View.VISIBLE
            val email = username!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@ForgotPasswordActivity, "Veuillez entrer un email", Toast.LENGTH_SHORT)
                    .show()
                loading.visibility = View.GONE
                return@setOnClickListener
            } else {
                val actionCodeSettings =
                    ActionCodeSettings.newBuilder().setHandleCodeInApp(false).setUrl("https://hypnotabac.page.link/AY2a").build()
                firebaseAuth.sendPasswordResetEmail(email, actionCodeSettings)
                loading.visibility = View.GONE
                Toast.makeText(this@ForgotPasswordActivity, "Email envoyé", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }
    }
}