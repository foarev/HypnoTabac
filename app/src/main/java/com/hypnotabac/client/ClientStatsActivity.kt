package com.hypnotabac.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.Date
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_c_stats.*
import kotlinx.android.synthetic.main.activity_c_stats.barChartViewCondition
import kotlinx.android.synthetic.main.activity_c_stats.barChartViewGrade
import kotlinx.android.synthetic.main.activity_c_stats.textView
import kotlinx.android.synthetic.main.status_bar_client.*
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
            .child(SaveSharedPreferences.getUserID(this))
            .child("cigs")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value!=null){
                    val cigs = dataSnapshot.value as ArrayList<*>
                    val numGrade = MutableList(3) { _ -> 0 }
                    val numCondition = mutableMapOf<String, Int>()
                    var totalCigs = 0
                    var dateMax = Date(1,1,1)
                    var dateMin = Date(9999,1,1)

                    cigs.forEach{c ->
                        val cig = c as Map<*, *>
                        val date = Date((cig["year"] as String).toInt(), (cig["month"] as String).toInt(), (cig["day"] as String).toInt())
                        if(date > dateMax) dateMax = date
                        if(date < dateMin) dateMin = date

                        numGrade[(cig["grade"] as Long).toInt()-1] += 1
                        if(numCondition.containsKey(cig["condition"] as String))
                            numCondition[cig["condition"] as String] = numCondition[cig["condition"] as String]?.plus(1) as Int
                        else
                            numCondition[cig["condition"] as String] = 1
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
                    xAxisValues1.forEach{k ->
                        yValueGroup1.add(BarEntry(k.toFloat(), numGrade[k.toInt()-1].toFloat()))
                    }

                    val barDataSet1 = BarDataSet(yValueGroup1, "Nombre de cigarettes par note")
                    barDataSet1.setGradientColor(ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimaryLight),
                        ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimary2))
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

                    barChartViewGrade.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    barChartViewGrade.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    barChartViewGrade.legend.orientation = Legend.LegendOrientation.VERTICAL
                    barChartViewGrade.legend.setDrawInside(true)
                    barChartViewGrade.legend.direction = Legend.LegendDirection.RIGHT_TO_LEFT

                    val xAxis1 = barChartViewGrade.xAxis
                    xAxis1.setDrawGridLines(false)
                    xAxis1.position = XAxis.XAxisPosition.BOTTOM
                    xAxis1.textSize = 9f
                    xAxis1.labelCount = xAxisValues1.count()
                    xAxis1.valueFormatter = LargeValueFormatter()
                    xAxis1.spaceMin = 4f
                    xAxis1.spaceMax = 4f

                    val leftAxis1 = barChartViewGrade.axisLeft
                    leftAxis1.valueFormatter = LargeValueFormatter()
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
                    numCondition.forEach{n ->
                        if(!xAxisValues2.contains(n.key))
                            xAxisValues2.add(n.key)
                    }
                    val yValueGroup2 = ArrayList<BarEntry>()
                    xAxisValues2.forEachIndexed{i, k ->
                        if(numCondition.containsKey(k))
                            yValueGroup2.add(BarEntry(i.toFloat(), numCondition[k]!!.toFloat()))
                        else
                            yValueGroup2.add(BarEntry(i.toFloat(), 0F))
                    }

                    val barDataSet2 = BarDataSet(yValueGroup2, "Nombre de cigarettes par condition")
                    barDataSet2.setGradientColor(ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimaryLight),
                        ContextCompat.getColor(this@ClientStatsActivity, R.color.colorPrimary2))
                    barDataSet2.setDrawIcons(false)
                    barDataSet2.setDrawValues(false)

                    val barData2 = BarData(barDataSet2)
                    barChartViewCondition.description.isEnabled = false
                    barChartViewCondition.description.textSize = 0f
                    barData2.setValueFormatter(LargeValueFormatter())
                    barChartViewCondition.data = barData2
                    barChartViewCondition.barData.barWidth = 0.8f
                    barChartViewCondition.xAxis.axisMinimum = -0.5f
                    barChartViewCondition.xAxis.axisMaximum = xAxisValues2.count().toFloat() - 1.5f
                    barChartViewCondition.data.isHighlightEnabled = false
                    barChartViewCondition.setVisibleXRange(1f, xAxisValues2.count().toFloat())

                    barChartViewCondition.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    barChartViewCondition.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    barChartViewCondition.legend.orientation = Legend.LegendOrientation.VERTICAL
                    barChartViewCondition.legend.setDrawInside(true)
                    barChartViewCondition.legend.direction = Legend.LegendDirection.RIGHT_TO_LEFT

                    val xAxis2 = barChartViewCondition.xAxis
                    xAxis2.setDrawGridLines(false)
                    xAxis2.position = XAxis.XAxisPosition.BOTTOM
                    xAxis2.textSize = 9f
                    xAxis2.labelCount = xAxisValues2.count()
                    xAxis2.valueFormatter = IndexAxisValueFormatter(xAxisValues2)
                    xAxis2.spaceMin = 4f
                    xAxis2.spaceMax = 4f

                    val leftAxis2 = barChartViewCondition.axisLeft
                    leftAxis2.valueFormatter =
                        /*IAxisValueFormatter { value, axis -> value.roundToInt().toString() } */ LargeValueFormatter()
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
