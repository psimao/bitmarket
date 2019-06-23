package com.psimao.bitmarket.charts.domain

import com.gojuno.koptional.Optional
import com.psimao.bitmarket.charts.domain.entity.ChartData
import com.psimao.bitmarket.charts.domain.repository.ChartsRepository
import com.psimao.bitmarket.domain.ReactiveInteractor
import com.psimao.bitmarket.domain.transformer.UnwrapOptionalTransformer
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RetrieveMarketPriceChartInteractor(
    private val repository: ChartsRepository
): ReactiveInteractor.RetrieveInteractor<String, ChartData> {

    override fun getStream(params: String): Flowable<ChartData> {
        return repository.getMarketPriceChartData()
            .flatMapSingle { chartData -> fetchWhenNone(chartData, params).andThen(Single.just(chartData)) }
            .compose(UnwrapOptionalTransformer())
    }

    private fun fetchWhenNone(chartData: Optional<ChartData>, timeSpan: String): Completable =
        chartData.toNullable()?.let { Completable.complete() } ?: run { repository.fetchMarketPriceChartData(timeSpan) }
}