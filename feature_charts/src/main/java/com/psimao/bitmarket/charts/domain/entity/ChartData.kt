package com.psimao.bitmarket.charts.domain.entity

data class ChartData(
    val type: String,
    val name: String,
    val timeSpan: String,
    val stats: BitcoinStats,
    val description: String,
    val entries: List<ChartEntry>
)