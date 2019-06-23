package com.psimao.bitmarket.domain

import io.reactivex.Completable
import io.reactivex.Flowable

interface ReactiveInteractor {

    interface RetrieveInteractor<Params, Object>: ReactiveInteractor {

        fun getStream(params: Params) : Flowable<Object>
    }

    interface RefreshInteractor<Params> : ReactiveInteractor {

        fun getRefreshCompletable(params: Params): Completable
    }
}