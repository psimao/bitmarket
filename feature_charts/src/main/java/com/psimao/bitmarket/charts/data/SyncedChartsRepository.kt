package com.psimao.bitmarket.charts.data

import com.gojuno.koptional.Optional
import com.psimao.bitmarket.charts.data.remote.ChartDataMapper
import com.psimao.bitmarket.charts.data.remote.ChartsAndStatsApi
import com.psimao.bitmarket.charts.data.remote.MarketPriceChartResponse
import com.psimao.bitmarket.charts.data.remote.StatsResponse
import com.psimao.bitmarket.charts.domain.entity.ChartData
import com.psimao.bitmarket.charts.domain.entity.ChartType
import com.psimao.bitmarket.charts.domain.repository.ChartsRepository
import com.psimao.bitmarket.data.ReactiveStore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

internal class SyncedChartsRepository(
    private val store: ReactiveStore<String, ChartData>,
    private val api: ChartsAndStatsApi
) : ChartsRepository {

    override fun getMarketPriceChartData(): Flowable<Optional<ChartData>> = store.get(
        ChartType.MARKET_PRICE)

    override fun fetchMarketPriceChartData(timeSpan: String): Completable =
        api.getMarketPriceChart(timeSpan)
            .zipWith(api.getStats(), zipChartAndStatsResponses(timeSpan))
            .doOnSuccess { chartData -> store.store(chartData) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .ignoreElement()

    private fun zipChartAndStatsResponses(timeSpan: String) =
        BiFunction<MarketPriceChartResponse, StatsResponse, ChartData> { chartResponse, statsResponse ->
            ChartDataMapper.transform(chartResponse, statsResponse, ChartType.MARKET_PRICE, timeSpan)
        }
}