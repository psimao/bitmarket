package com.psimao.bitmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psimao.bitmarket.feature.ChartsAndStatsFeature

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startChartAndStatsFeature()
        finish()
    }

    private fun startChartAndStatsFeature() {
        ChartsAndStatsFeature.dynamicStart?.let { startActivity(it) }
    }
}
