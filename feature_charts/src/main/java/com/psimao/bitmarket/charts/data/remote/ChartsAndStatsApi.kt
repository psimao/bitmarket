package com.psimao.bitmarket.charts.data.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ChartsAndStatsApi {

    @GET("charts/market-price?format=json")
    fun getMarketPriceChart(@Query("timespan") timeSpan: String): Single<MarketPriceChartResponse>

    @GET("stats?format=json")
    fun getStats(): Single<StatsResponse>
}