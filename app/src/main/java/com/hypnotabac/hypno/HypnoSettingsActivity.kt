package com.hypnotabac.hypno

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import com.hypnotabac.LoginActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.status_bar_hypno.*

class HypnoSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_settings)
        logout.setOnClickListener{
            SaveSharedPreferences.resetAll(this)
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
        settings.visibility = GONE
    }
}