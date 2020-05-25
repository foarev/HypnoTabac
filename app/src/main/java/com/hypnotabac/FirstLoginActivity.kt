package com.hypnotabac

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hypnotabac.client.ClientLoginActivity
import com.hypnotabac.client.ClientMainActivity
import com.hypnotabac.hypno.HypnoLoginActivity
import com.hypnotabac.hypno.HypnoMainActivity
import kotlinx.android.synthetic.main.activity_first_login.*

class FirstLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login)

        if (SaveSharedPreferences.getUserType(this) == "hypno" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
        } else if (SaveSharedPreferences.getUserType(this) == "client" && SaveSharedPreferences.getEmail(this) != "") {
            startActivity(Intent(applicationContext, ClientMainActivity::class.java))
        } else {
            hypno.setOnClickListener{
                startActivity(Intent(applicationContext, HypnoLoginActivity::class.java))
            }
            client.setOnClickListener{
                startActivity(Intent(applicationContext, ClientLoginActivity::class.java))
            }
        }
    }
}
