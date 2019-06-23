package com.psimao.bitmarket.charts.data.remote

import com.psimao.bitmarket.charts.domain.entity.ChartEntry

internal data class MarketPriceChartResponse(
    val status: String,
    val name: String,
    val unit: String,
    val period: String,
    val description: String,
    val values: List<ChartEntry>
)