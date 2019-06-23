package com.psimao.bitmarket.charts.data.remote

import com.psimao.bitmarket.charts.domain.entity.BitcoinStats

internal object BitcoinStatsMapper {

    fun transform(statsResponse: StatsResponse): BitcoinStats = BitcoinStats(statsResponse.marketPriceUsd)
}