package com.psimao.bitmarket.charts.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psimao.bitmarket.charts.RefreshMarketPriceChartInteractor
import com.psimao.bitmarket.charts.di.ChartsAndStatsComponent
import com.psimao.bitmarket.charts.domain.RetrieveMarketPriceChartInteractor
import com.psimao.bitmarket.charts.domain.entity.ChartData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject

class MarketPriceViewModel : ViewModel(), ChartsAndStatsComponent {

    companion object {

        private val DEFAULT_TIME_SPAN = TimeSpanOption.THIRTY_DAYS
    }

    private val retrieveMarketPriceChartInteractor: RetrieveMarketPriceChartInteractor by inject()
    private val refreshMarketPriceChartInteractor: RefreshMarketPriceChartInteractor by inject()

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    private val _marketPriceChart: MutableLiveData<ChartData> = MutableLiveData()

    val marketPriceChart: LiveData<ChartData>
        get() = _marketPriceChart

    init {
        disposables.add(bindToMarketPriceChart())
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun notifyTimeSpanChanged(timeSpanOption: TimeSpanOption) {
        disposables.add(refreshMarketPriceChart(timeSpanOption))
    }

    private fun onMarketPriceChartAvailable(chartData: ChartData) {
        _marketPriceChart.postValue(chartData)
    }

    private fun onMarketPriceChartError(throwable: Throwable) {
        Log.e(MarketPriceViewModel::class.java.simpleName, throwable.localizedMessage, throwable)
    }

    private fun bindToMarketPriceChart(): Disposable =
        retrieveMarketPriceChartInteractor.getStream(DEFAULT_TIME_SPAN.stringValue)
            .observeOn(Schedulers.computation())
            .subscribe(::onMarketPriceChartAvailable, ::onMarketPriceChartError)

    private fun refreshMarketPriceChart(timeSpanOption: TimeSpanOption): Disposable =
        refreshMarketPriceChartInteractor.getRefreshCompletable(timeSpanOption.stringValue)
            .observeOn(Schedulers.computation())
            .subscribe()
}