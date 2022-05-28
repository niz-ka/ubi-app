package com.example.boardgamecollector

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class BoardGameRankActivity : NavigationActivity() {

    companion object {
        private const val TAG = "BoardGameRankActivity"
        private val formatter = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
    }

    private lateinit var chart: LineChart
    private lateinit var ranks: List<Rank>
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_game_rank)
        create()
        supportActionBar?.title = getString(R.string.rankHistory)
        Log.i(TAG, "Creating activity")

        chart = findViewById(R.id.chart)
        titleTextView = findViewById(R.id.titleTextView)
        val id = intent.getLongExtra("id", 0)

        val game = Game.findOne(id)
        titleTextView.text = game?.title ?: "No title"

        ranks = Rank.findAllById(id).sortedBy { it.date }

        if(ranks.size < 2) {
            titleTextView.text = getString(R.string.noData, ranks.size.toString())
            return
        }

        val entries: MutableList<Entry> = ArrayList()

        for(i in ranks.indices) {
            entries.add(Entry(i.toFloat(), ranks[i].rank?.toFloat() ?: 0f))
        }

        val dataSet = LineDataSet(entries, "Rank")
        val lineData = LineData(dataSet)

        chart.description.isEnabled = false
        chart.xAxis.valueFormatter = MyXAxisFormatter()
        chart.xAxis.textColor = getColor(R.color.white)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.labelRotationAngle = 90f
        chart.xAxis.setLabelCount(entries.size, true)
        chart.xAxis.axisMinimum = entries[0].x
        chart.xAxis.axisMaximum = entries[entries.size-1].x

        chart.axisLeft.valueFormatter = MyYAxisFormatter()
        chart.axisLeft.isInverted = true
        chart.axisLeft.textColor = getColor(R.color.white)
        chart.axisLeft.granularity = 1.0f

        chart.axisRight.setDrawLabels(false)

        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        chart.legend.textColor = getColor(R.color.white)
        dataSet.valueTextColor = getColor(R.color.white)
        dataSet.valueTextSize = 10.0f

        chart.data = lineData
        chart.extraTopOffset = 5.0f
        chart.extraBottomOffset = 5.0f
        chart.extraLeftOffset = 5.0f
        chart.extraRightOffset = 5.0f
        chart.invalidate()

        Log.e(TAG, "$entries")
    }

    private inner class MyXAxisFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return formatter.format(ranks[value.roundToInt()].date ?: Date())
        }
    }

    private inner class MyYAxisFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return value.toInt().toString()
        }
    }
}