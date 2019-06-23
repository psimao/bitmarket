package com.psimao.bitmarket.charts.di

import com.psimao.bitmarket.charts.BuildConfig
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication

internal object ChartsAndStatsKoinApplication {

    val application = koinApplication {
        if (BuildConfig.DEBUG) androidLogger(Level.DEBUG)
        modules(ChartsAndStatsModules.all)
    }
}