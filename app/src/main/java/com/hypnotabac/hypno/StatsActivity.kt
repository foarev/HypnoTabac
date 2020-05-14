package com.hypnotabac.hypno

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hypnotabac.R

class StatsActivity : AppCompatActivity() {
    var clientID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        clientID = intent.getStringExtra("clientID")
    }
}
