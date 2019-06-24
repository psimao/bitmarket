package com.psimao.bitmarket.charts.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psimao.bitmarket.charts.di.ChartsAndStatsComponent
import com.psimao.bitmarket.charts.domain.RefreshMarketPriceChartInteractor
import com.psimao.bitmarket.charts.domain.RetrieveMarketPriceChartInteractor
import com.psimao.bitmarket.presentation.ViewEntityHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject

class MarketPriceViewModel : ViewModel(), ChartsAndStatsComponent {

    companion object {

        private val DEFAULT_TIME_SPAN = MarketPriceViewEntity.TimeSpanOption.THIRTY_DAYS
    }

    private val retrieveMarketPriceChartInteractor: RetrieveMarketPriceChartInteractor by inject()
    private val refreshMarketPriceChartInteractor: RefreshMarketPriceChartInteractor by inject()

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    private lateinit var marketPriceDisposable: Disposable

    private val _marketPriceEntityHolder: MutableLiveData<ViewEntityHolder<MarketPriceViewEntity>> = MutableLiveData()
    val marketPriceEntityHolder: LiveData<ViewEntityHolder<MarketPriceViewEntity>>
        get() = _marketPriceEntityHolder

    init {
        postLoading()
        bindToMarketPriceChart()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun notifyTimeSpanChanged(timeSpanOption: MarketPriceViewEntity.TimeSpanOption) {
        postLoading()
        if (marketPriceDisposable.isDisposed) bindToMarketPriceChart()
        disposables.add(refreshMarketPriceChart(timeSpanOption))
    }

    private fun postLoading() {
        _marketPriceEntityHolder.value = ViewEntityHolder.loading()
    }

    private fun onMarketPriceChartAvailable(viewEntity: MarketPriceViewEntity) {
        _marketPriceEntityHolder.postValue(ViewEntityHolder.create(viewEntity))
    }

    private fun onMarketPriceChartError(throwable: Throwable) {
        _marketPriceEntityHolder.postValue(ViewEntityHolder.withError(MarketPriceGeneralErrorViewEntity()))
        throwable.printStackTrace()
    }

    private fun bindToMarketPriceChart(): Disposable =
        retrieveMarketPriceChartInteractor.getStream(DEFAULT_TIME_SPAN.stringValue)
            .map(MarketPriceViewEntityMapper.create())
            .observeOn(Schedulers.computation())
            .subscribe(::onMarketPriceChartAvailable, ::onMarketPriceChartError)
            .also {  disposable ->
                disposables.add(disposable)
                marketPriceDisposable = disposable
            }

    private fun refreshMarketPriceChart(timeSpanOption: MarketPriceViewEntity.TimeSpanOption): Disposable =
        refreshMarketPriceChartInteractor.getRefreshCompletable(timeSpanOption.stringValue)
            .observeOn(Schedulers.computation())
            .subscribe({}, ::onMarketPriceChartError)
            .also {  disposable -> disposables.add(disposable) }
}