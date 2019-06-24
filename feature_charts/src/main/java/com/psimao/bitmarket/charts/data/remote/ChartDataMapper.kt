package com.psimao.bitmarket.charts.data.remote

import com.psimao.bitmarket.charts.domain.entity.ChartData

internal object ChartDataMapper {

    fun transform(
        chartResponse: MarketPriceChartResponse,
        statsResponse: StatsResponse,
        chartType: String,
        timeSpan: String
    ): ChartData = ChartData(
        type = chartType,
        name = chartResponse.name,
        timeSpan = timeSpan,
        stats = BitcoinStatsMapper.transform(statsResponse),
        description = chartResponse.description,
        entries = chartResponse.values
    )
}