package com.psimao.bitmarket.domain.transformer

import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import io.reactivex.*
import org.reactivestreams.Publisher

class UnwrapOptionalTransformer<T : Any> : FlowableTransformer<Optional<T>, T> {

    override fun apply(upstream: Flowable<Optional<T>>): Publisher<T> = upstream
        .filter { it is Some<T> }
        .map { it.toNullable() }
}