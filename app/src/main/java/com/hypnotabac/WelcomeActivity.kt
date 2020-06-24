package com.hypnotabac

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hypnotabac.hypno.HypnoSignupActivity
import com.hypnotabac.hypno.HypnoLoginActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        login.setOnClickListener {
            startActivity(Intent(applicationContext, HypnoLoginActivity::class.java))
        }
        signup.setOnClickListener{
            startActivity(Intent(applicationContext, HypnoSignupActivity::class.java))
        }
    }
}