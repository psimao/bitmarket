package com.psimao.bitmarket.feature

import android.content.Intent
import com.psimao.bitmarket.feature.loader.IntentLoader

object ChartsAndStatsFeature: DynamicFeature<Intent> {

    private const val CHARTS_AND_STATS = "com.psimao.bitmarket.charts.ChartsAndStatsActivity"

    override val dynamicStart: Intent?
        get() = IntentLoader.loadOrNull(CHARTS_AND_STATS)
}