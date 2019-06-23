package com.psimao.bitmarket.charts.data.remote

import com.psimao.bitmarket.charts.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal object ChartsAndStatsApiProvider {

    fun provideApi(baseUrl: String): ChartsAndStatsApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(setupOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ChartsAndStatsApi::class.java)

    private fun setupOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(getInterceptorLoggingLevel()))
        .build()

    private fun getInterceptorLoggingLevel(): HttpLoggingInterceptor.Level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}