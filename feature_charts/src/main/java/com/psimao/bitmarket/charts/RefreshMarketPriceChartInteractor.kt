package com.psimao.bitmarket.charts

import com.psimao.bitmarket.charts.domain.repository.ChartsRepository
import com.psimao.bitmarket.domain.ReactiveInteractor
import io.reactivex.Completable

class RefreshMarketPriceChartInteractor(
    private val repository: ChartsRepository
): ReactiveInteractor.RefreshInteractor<String> {

    override fun getRefreshCompletable(params: String): Completable =
        repository.fetchMarketPriceChartData(params)
}