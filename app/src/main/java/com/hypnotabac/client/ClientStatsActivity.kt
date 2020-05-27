package com.hypnotabac.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.Date
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_stats.*
import kotlin.math.roundToInt

class ClientStatsActivity : AppCompatActivity() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    val TAG = "ClientStatsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_stats)

        val dbRef = firebaseDatabase.getReference("users")
            .child(SaveSharedPreferences.getHypnoID(this))
            .child("clients")
            .child(firebaseAuth.currentUser!!.uid)
            .child("cigs")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value!=null){
                    val cigs = dataSnapshot.value as ArrayList<*>
                    val numGrade = MutableList(4) { _ -> 0 }
                    val numCondition = mutableMapOf("Ennui" to 0, "Café" to 0,"Pause" to 0,"Soirée" to 0,"Après manger" to 0,"Autre" to 0)
                    var totalCigs = 0
                    var dateMax = Date(1,1,1)
                    var dateMin = Date(9999,1,1)

                    cigs.forEach{c ->
                        val cig = c as Map<*, *>
                        val date = Date((cig["year"] as String).toInt(), (cig["month"] as String).toInt(), (cig["day"] as String).toInt())
                        if(date > dateMax) dateMax = date
                        if(date < dateMin) dateMin = date

                        numGrade[(cig["grade"] as Long).toInt()] += 1
                        if(numCondition.containsKey(cig["condition"] as String))
                            numCondition[cig["condition"] as String] = numCondition[cig["condition"] as String]?.plus(1) as Int
                        else
                            numCondition["Autre"] = numCondition["Autre"]?.plus(1) as Int
                        totalCigs += 1
                    }
                    val avg = (totalCigs.toFloat()/((dateMin..dateMax).count().toFloat())*100).roundToInt()/100.0

                    textView.text = "Vous avez consommé un total de $totalCigs cigarettes, soit environ $avg par jour."

                    // Graph 1
                    val xAxisValues1 = ArrayList<String>()
                    xAxisValues1.add("1")
                    xAxisValues1.add("2")
                    xAxisValues1.add("3")

                    val yValueGroup1 = ArrayList<BarEntry>()
                    numGrade.forEachIndexed{i, n ->
                        if(i>0)
                            yValueGroup1.add(BarEntry(i.toFloat(), n.toFloat()))
                    }

                    val barDataSet1 = BarDataSet(yValueGroup1, "Nombre de cigarettes par note")
                    barDataSet1.color = ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimary)
                    barDataSet1.setDrawIcons(false)
                    barDataSet1.setDrawValues(false)

                    val barData1 = BarData(barDataSet1)
                    barData1.setValueFormatter(LargeValueFormatter())
                    barChartViewGrade.description.isEnabled = false
                    barChartViewGrade.description.textSize = 0f
                    barChartViewGrade.data = barData1
                    barChartViewGrade.barData.barWidth = 0.8f
                    barChartViewGrade.data.isHighlightEnabled = false
                    barChartViewGrade.xAxis.axisMinimum = 0.5f
                    barChartViewGrade.xAxis.axisMaximum = 3.5f
                    barChartViewGrade.setVisibleXRange(1f, 3f)

                    val xAxis1 = barChartViewGrade.xAxis
                    xAxis1.setDrawGridLines(false)
                    xAxis1.position = XAxis.XAxisPosition.BOTTOM
                    xAxis1.textSize = 9f
                    xAxis1.labelCount = barDataSet1.entryCount
                    xAxis1.valueFormatter = IAxisValueFormatter { value, axis -> value.roundToInt().toString() }
                    xAxis1.spaceMin = 4f
                    xAxis1.spaceMax = 4f

                    val leftAxis1 = barChartViewGrade.axisLeft
                    leftAxis1.valueFormatter = IAxisValueFormatter { value, axis -> value.roundToInt().toString() }
                    leftAxis1.setDrawGridLines(false)
                    leftAxis1.labelCount = barDataSet1.yMax.roundToInt()
                    leftAxis1.spaceTop = 1f
                    leftAxis1.axisMinimum = 0f

                    val rightAxis1 = barChartViewGrade.axisRight
                    rightAxis1.setDrawGridLines(false)
                    rightAxis1.isEnabled = false
                    barChartViewGrade.invalidate()

                    // Graph 2
                    val xAxisValues2 = ArrayList<String>()
                    xAxisValues2.add("Ennui")
                    xAxisValues2.add("Café")
                    xAxisValues2.add("Pause")
                    xAxisValues2.add("Soirée")
                    xAxisValues2.add("Après manger")
                    xAxisValues2.add("Autre")

                    val yValueGroup2 = ArrayList<BarEntry>()
                    var index = 0
                    numCondition.forEach{n ->
                        yValueGroup2.add(BarEntry(index.toFloat(), n.value.toFloat()))
                        index += 1
                    }

                    val barDataSet2 = BarDataSet(yValueGroup2, "Nombre de cigarettes par condition")
                    barDataSet2.color = ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimary)
                    barDataSet2.setDrawIcons(false)
                    barDataSet2.setDrawValues(false)

                    val barData2 = BarData(barDataSet2)
                    barChartViewCondition.description.isEnabled = false
                    barChartViewCondition.description.textSize = 0f
                    barData2.setValueFormatter(LargeValueFormatter())
                    barChartViewCondition.data = barData2
                    barChartViewCondition.barData.barWidth = 0.8f
                    barChartViewCondition.xAxis.axisMinimum = -0.5f
                    barChartViewCondition.xAxis.axisMaximum = 5.5f
                    barChartViewCondition.data.isHighlightEnabled = false
                    barChartViewCondition.setVisibleXRange(1f, 6f)

                    val xAxis2 = barChartViewCondition.xAxis
                    xAxis2.setDrawGridLines(false)
                    xAxis2.position = XAxis.XAxisPosition.BOTTOM
                    xAxis2.textSize = 9f
                    xAxis2.labelCount = barDataSet2.entryCount
                    xAxis2.valueFormatter = IndexAxisValueFormatter(xAxisValues2)
                    xAxis2.spaceMin = 4f
                    xAxis2.spaceMax = 4f

                    val leftAxis2 = barChartViewCondition.axisLeft
                    leftAxis2.valueFormatter = IAxisValueFormatter { value, axis -> value.roundToInt().toString() }
                    leftAxis2.setDrawGridLines(false)
                    leftAxis2.labelCount = barDataSet2.yMax.roundToInt()
                    leftAxis2.spaceTop = 1f
                    leftAxis2.axisMinimum = 0f

                    val rightAxis2 = barChartViewCondition.axisRight
                    rightAxis2.setDrawGridLines(false)
                    rightAxis2.isEnabled = false

                    barChartViewCondition.invalidate()
                    dbRef.removeEventListener(this)
                }
                else
                    textView.text = "Vous n'avez pas encore consommé de cigarette."
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.w(TAG, "Failed to read value.", p0.toException())
            }
        })
    }
}
