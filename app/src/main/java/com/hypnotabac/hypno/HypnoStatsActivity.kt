package com.hypnotabac.hypno

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hypnotabac.R

class HypnoStatsActivity : AppCompatActivity() {
    var clientID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_stats)

        clientID = intent.getStringExtra("clientID")
    }
}
