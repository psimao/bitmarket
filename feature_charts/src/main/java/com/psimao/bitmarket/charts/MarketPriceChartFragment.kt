package com.psimao.bitmarket.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.psimao.bitmarket.charts.di.ChartsAndStatsComponent
import com.psimao.bitmarket.charts.presentation.MarketPriceViewEntity
import com.psimao.bitmarket.charts.presentation.MarketPriceViewModel
import com.psimao.bitmarket.charts.ui.BitmarketLineDataSet
import kotlinx.android.synthetic.main.fragment_market_price.*
import org.koin.android.viewmodel.ext.android.viewModel

class MarketPriceChartFragment : Fragment(), ChartsAndStatsComponent {

    private val marketPriceViewModel: MarketPriceViewModel by viewModel()

    private lateinit var chartComponentsGroup: List<View>
    private lateinit var progressGroup: List<View>
    private lateinit var errorGroup: List<View>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInteractionsListeners()
        setupGroups()
        registerObservers()
    }

    private fun onTimeSpanSelected(checkedId: Int, isChecked: Boolean) {
        if (isChecked) {
            getSelectedTimeSpanOption()?.let { timeSpanOption ->
                marketPriceViewModel.notifyTimeSpanChanged(timeSpanOption)
            }
        } else if (timeSpanChipGroup.checkedButtonId == View.NO_ID) {
            safeCheckTimeSpanButton(checkedId)
        }
    }

    private fun showMarketPrice(marketPrice: String) {
        if (currentPriceTextView.text.isEmpty()) {
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.feature_charts_fade_in_with_scale)
            currentPriceTextView.startAnimation(animation)
        }
        currentPriceTextView.text = marketPrice
    }

    private fun showChartDescription(description: String) {
        if (marketPriceDescriptionTextView.text.isEmpty()) {
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.feature_charts_fade_in)
            marketPriceDescriptionTextView.startAnimation(animation)
        }
        marketPriceDescriptionTextView.text = description
    }

    private fun showDataInChart(name: String, rawEntries: List<MarketPriceViewEntity.ChartEntry>) {
        val entries = rawEntries.map { Entry(it.x, it.y) }
        val dataSet = BitmarketLineDataSet(entries, name)
        val lineData = LineData(dataSet)
        marketPriceChart.data = lineData
        marketPriceChart.animateX(resources.getInteger(R.integer.feature_charts_duration_chart_animation))
        marketPriceChart.invalidate()
    }

    private fun setupUserInteractionsListeners() {
        timeSpanChipGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onTimeSpanSelected(checkedId, isChecked)
        }
    }

    private fun setupGroups() {
        chartComponentsGroup = listOf(currentPriceTextView, marketPriceDescriptionTextView, marketPriceChart)
        progressGroup = listOf(marketPriceChartProgressBar, marketPriceDescriptionProgressBar, currentPriceProgressBar)
        errorGroup = listOf(errorMessageTitleTextView, errorMessageDetailsTextView)
    }

    private fun setFilterButtonCheckedForTimeSpanOption(option: MarketPriceViewEntity.TimeSpanOption) {
        val buttonIdToBeChecked = getButtonIdForTimeSpanOption(option)
        if (timeSpanChipGroup.checkedButtonId != buttonIdToBeChecked) {
            safeCheckTimeSpanButton(buttonIdToBeChecked)
        }
    }

    private fun setLoadingState() {
        errorGroup.forEach { it.visibility = View.GONE }
        if (currentPriceTextView.text.isEmpty()) {
            currentPriceProgressBar.visibility = View.VISIBLE
            currentPriceTextView.visibility = View.INVISIBLE
        }
        if (marketPriceDescriptionTextView.text.isEmpty()) {
            marketPriceDescriptionProgressBar.visibility = View.VISIBLE
            currentPriceTextView.visibility = View.INVISIBLE
        }
        marketPriceChart.visibility = View.INVISIBLE
        marketPriceChartProgressBar.visibility = View.VISIBLE
    }

    private fun setErrorState() {
        marketPriceChart.visibility = View.INVISIBLE
        progressGroup.forEach { it.visibility = View.GONE }
        errorGroup.forEach { it.visibility = View.VISIBLE }
    }

    private fun setDataLoadedState(viewEntity: MarketPriceViewEntity) {
        errorGroup.forEach { it.visibility = View.GONE }
        progressGroup.forEach { it.visibility = View.GONE }
        chartComponentsGroup.forEach { it.visibility = View.VISIBLE }
        showMarketPrice(viewEntity.bitcoinValue)
        showChartDescription(viewEntity.description)
        setFilterButtonCheckedForTimeSpanOption(viewEntity.timeSpan)
        showDataInChart(viewEntity.name, viewEntity.entries)
    }

    private fun registerObservers() {
        marketPriceViewModel.marketPriceEntityHolder.observe(viewLifecycleOwner, Observer { entityHolder ->
            when {
                entityHolder.isLoading -> setLoadingState()
                entityHolder.hasError -> setErrorState()
                else -> entityHolder.entity?.let { entity -> setDataLoadedState(entity) }
            }
        })
    }

    private fun safeCheckTimeSpanButton(checkedButtonId: Int) {
        timeSpanChipGroup.clearOnButtonCheckedListeners()
        timeSpanChipGroup.check(checkedButtonId)
        timeSpanChipGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onTimeSpanSelected(checkedId, isChecked)
        }
    }

    private fun getSelectedTimeSpanOption(): MarketPriceViewEntity.TimeSpanOption? =
        when (timeSpanChipGroup.checkedButtonId) {
            R.id.thirtyDaysButton -> MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS
            R.id.threeMonthsButton -> MarketPriceViewEntity.TimeSpanOption.THREE_MONTHS
            R.id.oneYearButton -> MarketPriceViewEntity.TimeSpanOption.ONE_YEAR
            R.id.allTimeButton -> MarketPriceViewEntity.TimeSpanOption.ALL_TIME
            else -> null
        }

    private fun getButtonIdForTimeSpanOption(timeSpanOption: MarketPriceViewEntity.TimeSpanOption): Int =
        when (timeSpanOption) {
            MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS -> R.id.thirtyDaysButton
            MarketPriceViewEntity.TimeSpanOption.THREE_MONTHS -> R.id.threeMonthsButton
            MarketPriceViewEntity.TimeSpanOption.ONE_YEAR -> R.id.oneYearButton
            MarketPriceViewEntity.TimeSpanOption.ALL_TIME -> R.id.allTimeButton
        }
}
