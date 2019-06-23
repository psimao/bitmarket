package com.psimao.bitmarket.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.psimao.bitmarket.charts.di.ChartsAndStatsComponent
import com.psimao.bitmarket.charts.domain.entity.ChartEntry
import com.psimao.bitmarket.charts.presentation.MarketPriceViewModel
import com.psimao.bitmarket.charts.presentation.TimeSpanOption
import kotlinx.android.synthetic.main.fragment_market_price.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class MarketPriceChartFragment : Fragment(), ChartsAndStatsComponent {

    companion object {

        private const val STATE_CHECKED_BUTTON = "state_checked_button"
    }

    private val marketPriceViewModel: MarketPriceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInteractionsListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CHECKED_BUTTON, timeSpanChipGroup.checkedButtonId)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        timeSpanChipGroup.check(savedInstanceState?.getInt(STATE_CHECKED_BUTTON) ?: R.id.thirtyDaysButton)
    }

    private fun showMarketPrice(marketPrice: Double) {
        val formattedCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(marketPrice)
        currentPriceTextView.alpha = 0f
        currentPriceTextView.scaleX = 0.7f
        currentPriceTextView.scaleY = 0.7f
        currentPriceTextView.text = formattedCurrency
        currentPriceTextView.animate()
            .setDuration(900)
            .setInterpolator(DecelerateInterpolator())
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .withEndAction {
                currentPriceTextView.alpha = 1f
                currentPriceTextView.scaleX = 1f
                currentPriceTextView.scaleY = 1f
            }
            .start()
    }

    private fun showChartDescription(description: String) {
        marketPriceDescriptionTextView.alpha = 0f
        marketPriceDescriptionTextView.text = description
        marketPriceDescriptionTextView.animate()
            .setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(1f)
            .withEndAction { marketPriceDescriptionTextView.alpha = 1f }
            .start()
    }

    private fun showDataInChart(rawEntries: List<ChartEntry>) {
        val chartLabel = "Market Price"
        val entries = rawEntries.map { Entry(it.x.toFloat(), it.y.toFloat()) }

        marketPriceChart.setDrawGridBackground(false)
        marketPriceChart.setDrawBorders(false)
        marketPriceChart.description.isEnabled = false
        marketPriceChart.isAutoScaleMinMaxEnabled = true

        val dataSet = LineDataSet(entries, chartLabel).apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1f
            setColor(Color.WHITE, 255)
            setDrawFilled(true)
            setDrawValues(false)
            fillColor = Color.WHITE
            fillAlpha = 100
            setDrawCircles(false)
            lineWidth = 1.6f
        }

        val lineData = LineData(dataSet)
        marketPriceChart.data = lineData
        marketPriceChart.xAxis.valueFormatter = object : ValueFormatter() {

            val dateFormat = DateFormat.getDateInstance()

            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(Date(value.toLong()))
            }
        }

        marketPriceChart.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        marketPriceChart.axisLeft.apply {
            setDrawGridLines(false)
            //setLabelCount(8, true)
            setDrawAxisLine(false)
        }

        marketPriceChart.axisRight.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        marketPriceChart.setViewPortOffsets(0f, 0f, 0f, 0f)

        marketPriceChart.animateX(1500)
        marketPriceChart.invalidate()
    }

    private fun registerObservers() {
        marketPriceViewModel.marketPriceChart.observe(this, Observer { chartData ->
            showMarketPrice(chartData.stats.marketPrice)
            showChartDescription(chartData.description)
            showDataInChart(chartData.entries)
        })
    }

    private fun setupUserInteractionsListeners() {
        timeSpanChipGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                getSelectedTimeSpanOption()?.let { timeSpanOption ->
                    marketPriceViewModel.notifyTimeSpanChanged(timeSpanOption)
                }
            } else if (timeSpanChipGroup.checkedButtonId == View.NO_ID) {
                timeSpanChipGroup.check(checkedId)
            }
        }
    }

    private fun getSelectedTimeSpanOption(): TimeSpanOption? = when (timeSpanChipGroup.checkedButtonId) {
        R.id.thirtyDaysButton -> TimeSpanOption.THIRTY_DAYS
        R.id.threeMonthsButton -> TimeSpanOption.THREE_MONTHS
        R.id.oneYearButton -> TimeSpanOption.ONE_YEAR
        R.id.allTimeButton -> TimeSpanOption.ALL_TIME
        else -> null
    }
}
