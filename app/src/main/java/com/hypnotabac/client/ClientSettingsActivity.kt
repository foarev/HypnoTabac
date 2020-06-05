package com.hypnotabac.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import com.hypnotabac.R
import kotlinx.android.synthetic.main.status_bar_client.*

class ClientSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_settings)
        settings.visibility = GONE
    }
}
