package com.hypnotabac.hypno.stats

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.Date
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import kotlinx.android.synthetic.main.activity_h_stats.*
import kotlin.math.max
import kotlin.math.roundToInt


class HypnoStatsActivity : AppCompatActivity() {
    var clientID = ""
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val responsesAdapter: ResponsesAdapter = ResponsesAdapter()
    val TAG = "HypnoStatsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_stats)
        clientID = intent.getStringExtra("clientID")

        val dbRef = firebaseDatabase.getReference("users")
            .child(SaveSharedPreferences.getUserID(this))
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val root = dataSnapshot.value as Map<*,*>
                val clientsRoot = root["clients"] as Map<*,*>
                val clientRoot = clientsRoot[clientID] as Map<*,*>
                val firstName = clientRoot["firstName"] as String
                val lastName = clientRoot["lastName"] as String
                val questions = root["questions"] as ArrayList<*>
                val responses = clientRoot["responses"] as ArrayList<*>
                if(clientRoot["cigs"]!=null){
                    val cigs = clientRoot["cigs"] as ArrayList<*>
                    val numGrade = MutableList(3) { _ -> 0 }
                    val numCondition = mutableMapOf<String, Int>()
                    val numGradeCondition = mutableMapOf<String, MutableList<Int>>()
                    val numDate:MutableMap<Date, Int> = mutableMapOf()
                    var totalCigs = 0
                    var dateMax = Date(1,1,1)
                    var dateMin = Date(9999,1,1)

                    cigs.forEach{c ->
                        val cig = c as Map<*, *>
                        val date = Date((cig["year"] as String).toInt(), (cig["month"] as String).toInt(), (cig["day"] as String).toInt())
                        if(date > dateMax) dateMax = date
                        if(date < dateMin) dateMin = date

                        if(numDate.containsKey(date))
                            numDate[date] = numDate[date]?.plus(1) as Int
                        else
                            numDate[date] = 1

                        numGrade[(cig["grade"] as Long).toInt()-1] += 1
                        if(numCondition.containsKey(cig["condition"] as String))
                            numCondition[cig["condition"] as String] = numCondition[cig["condition"] as String]?.plus(1) as Int
                        else
                            numCondition[cig["condition"] as String] = 1

                        if(numGradeCondition.containsKey(cig["condition"] as String))
                            (numGradeCondition[cig["condition"] as String])!![(cig["grade"] as Long).toInt()-1] +=1
                        else{
                            numGradeCondition[cig["condition"] as String] = mutableListOf(0,0,0)
                            (numGradeCondition[cig["condition"] as String])!![(cig["grade"] as Long).toInt()-1] +=1
                        }
                        Log.w(TAG, numGradeCondition.toString())

                        totalCigs += 1
                    }
                    val avg = (totalCigs.toFloat()/((dateMin..dateMax).count().toFloat())*100).roundToInt()/100.0

                    textView.text = "$firstName $lastName a consommé un total de $totalCigs cigarettes, soit environ $avg par jour."

                    val xAxisValues = ArrayList<String>()

                    //Graph days
                    val entries = ArrayList<Entry>()
                    var indexDate = 0
                    (dateMin..dateMax).forEach{d ->
                        if(numDate.containsKey(d))
                            entries.add(Entry(indexDate.toFloat(), numDate[d]!!.toFloat()))
                        else
                            entries.add(Entry(indexDate.toFloat(), 0F))
                        xAxisValues.add(d.toString())
                        indexDate += 1
                    }
                    val vl = LineDataSet(entries, "Evolution du nombre de cigarettes")
                    vl.setDrawValues(false)
                    vl.setDrawFilled(true)
                    vl.lineWidth = 3f
                    vl.fillDrawable = ContextCompat.getDrawable(this@HypnoStatsActivity, R.drawable.gradient_bg_graphs)
                    lineChart.xAxis.labelRotationAngle = 0f
                    lineChart.xAxis.setDrawGridLines(false)
                    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    lineChart.xAxis.textSize = 9f
                    lineChart.xAxis.labelCount = xAxisValues.count()-1
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
                    lineChart.xAxis.spaceMin = 4f
                    lineChart.xAxis.spaceMax = 4f
                    lineChart.xAxis.axisMaximum = xAxisValues.count()-1f
                    lineChart.xAxis.axisMinimum = 0f

                    lineChart.axisLeft.valueFormatter = LargeValueFormatter()
                    lineChart.axisLeft.setDrawGridLines(false)
                    lineChart.axisLeft.labelCount = numDate.values.max()!!
                    lineChart.axisLeft.spaceTop = 1f
                    lineChart.axisLeft.axisMinimum = 0f
                    lineChart.data = LineData(vl)
                    lineChart.axisRight.isEnabled = false
                    lineChart.description.isEnabled = false
                    lineChart.animateX(10, Easing.EaseInExpo)


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
                    barDataSet1.setGradientColor(ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimaryLight),
                        ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2))
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
                    //barDataSet2.color = ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2)
                    barDataSet2.setGradientColor(ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimaryLight),
                        ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2))
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


                    Log.w(TAG, numGradeCondition.toString())

                    // Graph 3
                    val xAxisValues3 = ArrayList<String>()
                    numGradeCondition.forEach{n ->
                        if(!xAxisValues3.contains(n.key))
                            xAxisValues3.add(n.key)
                    }
                    val yValueGroup31 = ArrayList<BarEntry>()
                    val yValueGroup32 = ArrayList<BarEntry>()
                    val yValueGroup33 = ArrayList<BarEntry>()
                    xAxisValues3.forEachIndexed{i, k ->
                        if(numGradeCondition.containsKey(k)) {
                            yValueGroup31.add(BarEntry(i.toFloat(), numGradeCondition[k]!![0].toFloat()))
                            yValueGroup32.add(BarEntry(i.toFloat(), numGradeCondition[k]!![1].toFloat()))
                            yValueGroup33.add(BarEntry(i.toFloat(), numGradeCondition[k]!![2].toFloat()))
                        }
                        else {
                            xAxisValues3.remove(k)
                        }
                    }
                    Log.w(TAG, "1 : "+yValueGroup31.toString())
                    Log.w(TAG, "2 : "+yValueGroup32.toString())
                    Log.w(TAG, "3 : "+yValueGroup33.toString())

                    val barDataSet31 = BarDataSet(yValueGroup31, "Cigarettes notées 1")
                    val barDataSet32 = BarDataSet(yValueGroup32, "Cigarettes notées 2")
                    val barDataSet33 = BarDataSet(yValueGroup33, "Cigarettes notées 3")

                    barDataSet31.color = ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary)
                    barDataSet32.color = ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2)
                    barDataSet33.color = ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary3)
                    barDataSet31.setGradientColor(ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimaryLight),
                        ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary))
                    barDataSet32.setGradientColor(ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2Light),
                        ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary2))
                    barDataSet33.setGradientColor(ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary3Light),
                        ContextCompat.getColor(this@HypnoStatsActivity, R.color.colorPrimary3))

                    barDataSet31.setDrawIcons(false)
                    barDataSet32.setDrawIcons(false)
                    barDataSet33.setDrawIcons(false)
                    barDataSet31.setDrawValues(false)
                    barDataSet32.setDrawValues(false)
                    barDataSet33.setDrawValues(false)


                    val barData3 = BarData(barDataSet31, barDataSet32, barDataSet33)
                    barChartViewGradeCondition.description.isEnabled = false
                    barChartViewGradeCondition.description.textSize = 0f
                    barData3.setValueFormatter(LargeValueFormatter())
                    barChartViewGradeCondition.data = barData3
                    val barSpace = 0.0f
                    val groupSpace = 0.1f
                    //(barSpace + barWidth)*3 + groupSpace = 1
                    //(0.05 + 0.25)*3 + 0.1
                    val barWidth = 0.3f
                    barChartViewGradeCondition.barData.barWidth = barWidth
                    barChartViewGradeCondition.groupBars(0f, groupSpace, barSpace)
                    barChartViewGradeCondition.xAxis.axisMinimum =  0.0f
                    barChartViewGradeCondition.xAxis.axisMaximum = xAxisValues3.count().toFloat()
                    barChartViewGradeCondition.data.isHighlightEnabled = false
                    barChartViewGradeCondition.setVisibleXRange(1f, xAxisValues3.count().toFloat())

                    barChartViewGradeCondition.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    barChartViewGradeCondition.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    barChartViewGradeCondition.legend.orientation = Legend.LegendOrientation.VERTICAL
                    barChartViewGradeCondition.legend.setDrawInside(true)
                    barChartViewGradeCondition.legend.direction = Legend.LegendDirection.RIGHT_TO_LEFT

                    val xAxis3 = barChartViewGradeCondition.xAxis
                    xAxis3.position = XAxis.XAxisPosition.BOTTOM
                    xAxis3.textSize = 9f
                    xAxis3.labelCount = xAxisValues3.count()
                    xAxis3.valueFormatter = IndexAxisValueFormatter(xAxisValues3)
                    xAxis3.spaceMin = 4f
                    xAxis3.spaceMax = 4f
                    xAxis3.setCenterAxisLabels(true)
                    xAxis3.setDrawGridLines(false)

                    val leftAxis3 = barChartViewGradeCondition.axisLeft
                    leftAxis3.valueFormatter = LargeValueFormatter()
                    leftAxis3.setDrawGridLines(false)
                    leftAxis3.labelCount = max(barDataSet31.yMax.roundToInt(), max(barDataSet32.yMax.roundToInt(), barDataSet33.yMax.roundToInt()))
                    leftAxis3.spaceTop = 1f
                    leftAxis3.axisMinimum = 0f

                    val rightAxis3 = barChartViewGradeCondition.axisRight
                    rightAxis3.setDrawGridLines(false)
                    rightAxis3.isEnabled = false


                    barChartViewGradeCondition.invalidate()
                    dbRef.removeEventListener(this)
                }
                else{
                    textView.text = "Ce client n'a pas encore consommé de cigarette."
                    barChartViewGrade.visibility = GONE
                    barChartViewCondition.visibility = GONE
                    lineChart.visibility = GONE
                }
                responses.forEachIndexed {i, r -> responsesAdapter.models.add(ResponsesView.Model(questions[i] as String, r as String)) }

                my_recycler_view.adapter = responsesAdapter
                my_recycler_view.layoutManager = LinearLayoutManager(this@HypnoStatsActivity)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.w(TAG, "Failed to read value.", p0.toException())
            }
        })

    }
}
