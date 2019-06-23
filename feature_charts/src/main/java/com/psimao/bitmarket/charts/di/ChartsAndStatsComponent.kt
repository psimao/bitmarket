package com.psimao.bitmarket.charts.di

import org.koin.core.Koin
import org.koin.core.KoinComponent

internal interface ChartsAndStatsComponent: KoinComponent {

    override fun getKoin(): Koin = ChartsAndStatsKoinApplication.application.koin
}