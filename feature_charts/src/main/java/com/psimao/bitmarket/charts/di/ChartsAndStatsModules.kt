package com.psimao.bitmarket.charts.di

import com.psimao.bitmarket.charts.BuildConfig
import com.psimao.bitmarket.charts.domain.RefreshMarketPriceChartInteractor
import com.psimao.bitmarket.charts.data.SyncedChartsRepository
import com.psimao.bitmarket.charts.data.remote.ChartsAndStatsApiProvider
import com.psimao.bitmarket.charts.data.store.ChartStore
import com.psimao.bitmarket.charts.domain.RetrieveMarketPriceChartInteractor
import com.psimao.bitmarket.charts.domain.repository.ChartsRepository
import com.psimao.bitmarket.charts.presentation.MarketPriceViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal object ChartsAndStatsModules {

    private val networkModule = module {
        single { ChartsAndStatsApiProvider.provideApi(BuildConfig.BLOCK_CHAIN_API_URL) }
    }

    private val storeModule = module {
        single { ChartStore.create() }
    }

    private val repositoryModule = module {
        factory<ChartsRepository> { SyncedChartsRepository(get(), get()) }
    }

    private val interactorModule = module {
        factory { RetrieveMarketPriceChartInteractor(get()) }
        factory { RefreshMarketPriceChartInteractor(get()) }
    }

    private val viewModelModule = module {
        viewModel { MarketPriceViewModel() }
    }

    val all = listOf(networkModule, storeModule, repositoryModule, interactorModule, viewModelModule)
}