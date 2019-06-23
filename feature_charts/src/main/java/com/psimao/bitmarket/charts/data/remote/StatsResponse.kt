package com.psimao.bitmarket.charts.data.remote

import com.google.gson.annotations.SerializedName

internal data class StatsResponse(
    @SerializedName("market_price_usd") val marketPriceUsd: Double
)