package com.hypnotabac

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hypnotabac.client.*
import com.hypnotabac.hypno.*
import kotlinx.android.synthetic.main.activity_first_login.*

class FirstLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login)

        hypno.setOnClickListener{
            startActivity(Intent(applicationContext, HypnoLoginActivity::class.java))
        }
        client.setOnClickListener{
            startActivity(Intent(applicationContext, ClientLoginActivity::class.java))
        }

    }
}
