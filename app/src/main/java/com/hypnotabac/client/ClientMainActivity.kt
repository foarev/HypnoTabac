package com.hypnotabac.client

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.FirstLoginActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_main.*
import org.xmlpull.v1.XmlPullParser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ClientMainActivity : AppCompatActivity() {
    val TAG = "ClientMainActivity"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    var grade = 0
    var condition = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_main)

        logout.setOnClickListener{
            SaveSharedPreferences.resetAll(this)
            startActivity(Intent(applicationContext, FirstLoginActivity::class.java))
        }

        settings.setOnClickListener{
            startActivity(Intent(applicationContext, ClientSettingsActivity::class.java))
        }

        addcig.setOnClickListener{
            addLayout.visibility = GONE
            gradeLayout.visibility = VISIBLE
        }
        btn1.setOnClickListener {
            grade = 1
            resetButtonTint1()
            btn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btn2.setOnClickListener {
            grade = 2
            resetButtonTint1()
            btn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btn3.setOnClickListener {
            grade = 3
            resetButtonTint1()
            btn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        next.setOnClickListener {
            if(grade!=0){
                gradeLayout.visibility = GONE
                conditionsLayout.visibility = VISIBLE
            } else {
                Toast.makeText(this, "Veuillez choisir une note", Toast.LENGTH_SHORT).show()
            }
        }

        btnCafe.setOnClickListener {
            condition = "Café"
            resetButtonTint2()
            btnCafe.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btnEnnui.setOnClickListener {
            condition = "Ennui"
            resetButtonTint2()
            btnEnnui.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btnPause.setOnClickListener {
            condition = "Pause"
            resetButtonTint2()
            btnPause.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btnApresManger.setOnClickListener {
            condition = "Après manger"
            resetButtonTint2()
            btnApresManger.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btnSoiree.setOnClickListener {
            condition = "Soirée"
            resetButtonTint2()
            btnSoiree.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btnAutre.setOnClickListener {
            condition = "Autre"
            resetButtonTint2()
            btnAutre.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        done.setOnClickListener {
            if(condition!=""){
                if(condition == "Autre") condition = other.text.toString()
                conditionsLayout.visibility = GONE
                addLayout.visibility = VISIBLE
                if(SaveSharedPreferences.getHypnoID(this)!="" && SaveSharedPreferences.getUserID(this)!="") {
                    val dbRef = firebaseDatabase.reference.child("users")
                        .child(SaveSharedPreferences.getHypnoID(this))
                        .child("clients")
                        .child(SaveSharedPreferences.getUserID(this))
                        .child("cigs")
                    dbRef.addValueEventListener(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val newIndex = ""+dataSnapshot.childrenCount
                            dbRef.child(newIndex).child("grade").setValue(grade)
                            dbRef.child(newIndex).child("condition").setValue(condition)

                            val currentTime = LocalDateTime.now()

                            val year = currentTime.format(DateTimeFormatter.ofPattern("yy"))
                            val month = currentTime.format(DateTimeFormatter.ofPattern("MM"))
                            val day = currentTime.format(DateTimeFormatter.ofPattern("dd"))
                            val hour = currentTime.format(DateTimeFormatter.ofPattern("HH"))
                            val minute = currentTime.format(DateTimeFormatter.ofPattern("mm"))

                            dbRef.child(newIndex).child("year").setValue(year)
                            dbRef.child(newIndex).child("month").setValue(month)
                            dbRef.child(newIndex).child("day").setValue(day)
                            dbRef.child(newIndex).child("hour").setValue(hour)
                            dbRef.child(newIndex).child("minute").setValue(minute)
                            dbRef.removeEventListener(this)
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            Log.w(TAG, "Failed to read value.", p0.toException())
                        }
                    })
                } else {
                    Toast.makeText(this, "Une erreur est survenue.", Toast.LENGTH_SHORT).show()
                    SaveSharedPreferences.resetAll(this)
                    startActivity(Intent(applicationContext, FirstLoginActivity::class.java))
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une condition", Toast.LENGTH_SHORT).show()
            }
        }
        statsBg.setOnClickListener {
            startActivity(Intent(applicationContext, ClientStatsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if(gradeLayout.visibility == VISIBLE){
            addLayout.visibility = VISIBLE
            gradeLayout.visibility = GONE
        }
        else if(conditionsLayout.visibility == VISIBLE){
            gradeLayout.visibility = VISIBLE
            conditionsLayout.visibility = GONE
        }
    }

    fun resetButtonTint1() {
        btn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
    }
    fun resetButtonTint2(){
        btnCafe.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btnEnnui.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btnPause.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btnApresManger.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btnSoiree.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        btnAutre.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
    }
}
