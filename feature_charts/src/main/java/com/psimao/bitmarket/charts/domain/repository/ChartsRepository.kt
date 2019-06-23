package com.psimao.bitmarket.charts.domain.repository

import com.gojuno.koptional.Optional
import com.psimao.bitmarket.charts.domain.entity.ChartData
import io.reactivex.Completable
import io.reactivex.Flowable

interface ChartsRepository {

    fun getMarketPriceChartData(): Flowable<Optional<ChartData>>

    fun fetchMarketPriceChartData(timeSpan: String): Completable
}