package com.hypnotabac.client

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.LoginActivity
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_main.*
import kotlinx.android.synthetic.main.condition_button_layout.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ClientMainActivity : AppCompatActivity() {
    val TAG = "ClientMainActivity"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    var grade = 0
    var condition = ""
    val conditions_list = arrayOf(
        "Récompense/Motivation",
        "Café",
        "Alcool/Soirée",
        "Après un repas",
        "Pause",
        "Avant le coucher",
        "Voiture",
        "Téléphone/Internet",
        "Film"
    )
    private var layoutManager: GridLayoutManager? = null
    private var adapter : SimpleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_main)

        layoutManager = GridLayoutManager(this, 3)
        (layoutManager as GridLayoutManager).spanSizeLookup = object:
            GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == conditions_list.size+1) {
                    return 3 // the item in position now takes up 4 spans
                }
                return 1
            }
        }
        adapter = SimpleAdapter(layoutManager, this, conditions_list, other, 3)
        buttonsRecyclerView.layoutManager = layoutManager
        buttonsRecyclerView.adapter = adapter

        buttonsRecyclerView.adapter!!.notifyDataSetChanged()

        addcig.setOnClickListener{
            addLayout.visibility = GONE
            gradeLayout.visibility = VISIBLE
        }
        btn1.setOnClickListener {
            grade = 1
            resetButtonTint1()
            btn1.setImageResource(R.drawable.btn1_full)
            //btn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btn2.setOnClickListener {
            grade = 2
            resetButtonTint1()
            btn2.setImageResource(R.drawable.btn2_full)
            //btn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        btn3.setOnClickListener {
            grade = 3
            resetButtonTint1()
            btn3.setImageResource(R.drawable.btn3_full)
            //btn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary2))
        }
        next.setOnClickListener {
            if(grade!=0){
                gradeLayout.visibility = GONE
                conditionsLayout.visibility = VISIBLE
            } else {
                Toast.makeText(this, "Veuillez choisir une note", Toast.LENGTH_SHORT).show()
            }
        }
        done.setOnClickListener {
            condition = adapter!!.condition
            if(condition!=""){
                resetButtonTint1()
                resetButtonTint2()
                if(condition == getString(R.string.other) && other.text.toString()!="") condition = other.text.toString()
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
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une condition", Toast.LENGTH_SHORT).show()
            }
        }
        stats.setOnClickListener {
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
        btn1.setImageResource(R.drawable.btn1)
        btn2.setImageResource(R.drawable.btn2)
        btn3.setImageResource(R.drawable.btn3)
    }
    fun resetButtonTint2(){
        adapter!!.resetButtonsTint()
        other.visibility = GONE
    }

}

class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.condition_button_layout, parent, false))
}

class SimpleAdapter(
    private val layoutManager: GridLayoutManager? = null,
    private val context: Context,
    private val conditions_list: Array<String>,
    private val other: EditText,
    private val spanCount:Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ButtonViewHolder(parent)
    }
    val buttons:MutableList<Button> = mutableListOf()
    var condition:String = ""
    var TAG:String = "SimpleAdapter"

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val linearLayout = p0.itemView as LinearLayout
        val btns = mutableListOf<Button>()
        btns.add(linearLayout.btn_condition1 as Button)
        btns.add(linearLayout.btn_condition2 as Button)
        btns.add(linearLayout.btn_condition3 as Button)
        (0..spanCount-1).forEach { index ->
            val position = (p1-index)/spanCount
            Log.w(TAG, "position : "+position+"; index : "+index+"; p1 : "+p1)
            if(p1>conditions_list.lastIndex)
                btns[index].text = context.getString(R.string.other)
            else
                btns[index].text = conditions_list[p1]
            buttons.add(btns[index])

            btns[index].setOnClickListener {
                if(p1>conditions_list.lastIndex){
                    condition = context.getString(R.string.other)
                    other.visibility = VISIBLE
                }
                else{
                    condition = conditions_list[p1]
                    other.visibility = GONE
                }
                resetButtonsTint()
                btns[index].setBackgroundResource(R.drawable.button)
            }
        }
    }
    fun resetButtonsTint(){
        buttons.forEach{it.setBackgroundResource(R.drawable.button_notpressed)}
    }
    override fun getItemCount() = conditions_list.size+1
}