package com.psimao.bitmarket.charts.presentation

import com.psimao.bitmarket.presentation.ViewEntity

data class MarketPriceViewEntity(
    val name: String,
    val timeSpan: TimeSpanOption,
    val bitcoinValue: String,
    val description: String,
    val entries: List<ChartEntry>
) : ViewEntity() {

    enum class TimeSpanOption(val stringValue: String) {

        THIRTY_DAYS("30days"),
        THREE_MONTHS("3months"),
        ONE_YEAR("1year"),
        ALL_TIME("all");
    }

    data class ChartEntry(val x: Float, val y: Float)
}