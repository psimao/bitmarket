package com.psimao.bitmarket.charts.presentation

import com.psimao.bitmarket.charts.domain.entity.ChartData
import com.psimao.bitmarket.charts.domain.entity.ChartEntry
import io.reactivex.functions.Function
import java.text.NumberFormat
import java.util.*

class MarketPriceViewEntityMapper private constructor(): Function<ChartData, MarketPriceViewEntity> {

    companion object {

        fun create(): MarketPriceViewEntityMapper = MarketPriceViewEntityMapper()
    }

    override fun apply(chartData: ChartData): MarketPriceViewEntity = MarketPriceViewEntity(
        name = chartData.name,
        timeSpan = getTimeSpanOptionForString(chartData.timeSpan),
        bitcoinValue = formattedBitcoinValue(chartData.stats.marketPrice),
        description = chartData.description,
        entries = mapChartEntries(chartData.entries)
    )

    private fun getTimeSpanOptionForString(timeSpan: String): MarketPriceViewEntity.TimeSpanOption =
        when (timeSpan) {
            MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS.stringValue ->
                MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS
            MarketPriceViewEntity.TimeSpanOption.THREE_MONTHS.stringValue ->
                MarketPriceViewEntity.TimeSpanOption.THREE_MONTHS
            MarketPriceViewEntity.TimeSpanOption.ONE_YEAR.stringValue ->
                MarketPriceViewEntity.TimeSpanOption.ONE_YEAR
            MarketPriceViewEntity.TimeSpanOption.ALL_TIME.stringValue ->
                MarketPriceViewEntity.TimeSpanOption.ALL_TIME
            else -> MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS
        }

    private fun formattedBitcoinValue(value: Double) =
        NumberFormat.getCurrencyInstance(Locale.US).format(value)

    private fun mapChartEntries(entries: List<ChartEntry>): List<MarketPriceViewEntity.ChartEntry> =
            entries.map { MarketPriceViewEntity.ChartEntry(it.x.toFloat(), it.y.toFloat()) }
}