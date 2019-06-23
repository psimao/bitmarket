package com.psimao.bitmarket.data

import com.gojuno.koptional.Optional
import io.reactivex.Flowable

interface ReactiveStore<Key, Value : Any> {

    fun getAll(): Flowable<Optional<List<Value>>>

    fun get(key: Key): Flowable<Optional<Value>>

    fun store(model: Value)

    fun storeAll(modelList: List<Value>)

    fun replaceAll(modelList: List<Value>)
}