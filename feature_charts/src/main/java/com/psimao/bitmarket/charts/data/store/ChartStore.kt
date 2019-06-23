package com.psimao.bitmarket.charts.data.store

import com.psimao.bitmarket.charts.domain.entity.ChartData
import com.psimao.bitmarket.data.MemoryCacheStore
import com.psimao.bitmarket.data.ReactiveStore

object ChartStore {

    fun create(): ReactiveStore<String, ChartData> = MemoryCacheStore { chartData -> chartData.type }
}