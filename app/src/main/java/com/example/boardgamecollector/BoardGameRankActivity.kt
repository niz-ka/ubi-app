package com.example.boardgamecollector

import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.Chart
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

        chart = findViewById(R.id.chart)
        titleTextView = findViewById(R.id.titleTextView)

        val id = intent.getLongExtra(App.INTENT_EXTRA_ID, 0)
        val game = Game.findOne(id)

        titleTextView.text = game?.title ?: getString(R.string.noTitle)
        ranks = Rank.findAllById(id).sortedBy { it.date }

        chart.setNoDataText(getString(R.string.noData, ranks.size.toString()))
        chart.getPaint(Chart.PAINT_INFO).color = getColor(R.color.black)
        chart.getPaint(Chart.PAINT_INFO).textSize = 40f

        if (ranks.size < 2) return

        val entries = ranks.mapIndexed { i, rank ->
            Entry(i.toFloat(), rank.rank?.toFloat() ?: 0f)
        }

        val dataSet = LineDataSet(entries, getString(R.string.rank))
        val lineData = LineData(dataSet)

        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 5f, 5f, 5f)

        val xAxis = chart.xAxis
        xAxis.valueFormatter = MyXAxisFormatter()
        xAxis.textColor = getColor(R.color.black)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 90f
        xAxis.setLabelCount(entries.size, true)
        xAxis.axisMinimum = entries[0].x
        xAxis.axisMaximum = entries[entries.size - 1].x

        val axisLeft = chart.axisLeft
        axisLeft.valueFormatter = MyYAxisFormatter()
        axisLeft.isInverted = true
        axisLeft.textColor = getColor(R.color.black)
        axisLeft.granularity = 1.0f

        chart.axisRight.setDrawLabels(false)

        val legend = chart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.textColor = getColor(R.color.black)

        dataSet.valueTextColor = getColor(R.color.black)
        dataSet.valueTextSize = 10.0f
        dataSet.lineWidth = 2.0f
        dataSet.color = getColor(R.color.teal_200)
        dataSet.circleColors = arrayListOf(getColor(R.color.teal_700))

        chart.data = lineData
        chart.invalidate()
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