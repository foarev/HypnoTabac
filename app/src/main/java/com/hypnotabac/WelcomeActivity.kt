package com.hypnotabac

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hypnotabac.client.ClientMainActivity
import com.hypnotabac.hypno.HypnoMainActivity
import com.hypnotabac.hypno.HypnoSignupActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (SaveSharedPreferences.getUserType(this) == "hypno" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
        } else if (SaveSharedPreferences.getUserType(this) == "client" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
        } else {
            login.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            signup.setOnClickListener{
                startActivity(Intent(applicationContext, HypnoSignupActivity::class.java))
            }
        }
    }
}