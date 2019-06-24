package com.psimao.bitmarket.data

import com.gojuno.koptional.Optional
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class MemoryCacheStore<Key, Value : Any>(private val extractKeyFromValue: (Value) -> Key) : ReactiveStore<Key, Value> {

    private val cache = ConcurrentHashMap<Key, Value>()

    private val singleSubjectsMap by lazy { HashMap<Key, Subject<Optional<Value>>>() }
    private val allSubject: Subject<Optional<List<Value>>>
            by lazy { PublishSubject.create<Optional<List<Value>>>().toSerialized() }

    override fun get(key: Key): Flowable<Optional<Value>> =
        Flowable.defer {
            getOrCreateSingleSubject(key)
                .startWith(Optional.toOptional(cache[key]))
                .toFlowable(BackpressureStrategy.DROP)
        }

    override fun getAll(): Flowable<Optional<List<Value>>> =
        Flowable.defer {
            allSubject
                .startWith(Optional.toOptional(getElementsOrNull()))
                .toFlowable(BackpressureStrategy.DROP)
        }

    override fun store(model: Value) {
        Completable.create { emitter ->
            storeOnCacheAndPublish(model)
            emitter.onComplete()
        }.subscribeOn(Schedulers.computation()).subscribe()
    }

    override fun storeAll(modelList: List<Value>) {
        Completable.create { emitter ->
            storeAllOnCacheAndPublish(modelList)
            emitter.onComplete()
        }.subscribeOn(Schedulers.computation()).subscribe()
    }

    override fun replaceAll(modelList: List<Value>) {
        cache.clear()
        storeAll(modelList)
    }

    private fun getOrCreateSingleSubject(key: Key): Subject<Optional<Value>> =
        synchronized(singleSubjectsMap) {
            singleSubjectsMap[key] ?: let {
                PublishSubject.create<Optional<Value>>().toSerialized()
                    .also { subject -> singleSubjectsMap[key] = subject }
            }
        }

    private fun storeOnCacheAndPublish(model: Value) {
        extractKeyFromValue.invoke(model).also { key ->
            cache[key] = model
            synchronized(singleSubjectsMap) { singleSubjectsMap[key]?.onNext(Optional.toOptional(model)) }
            allSubject.onNext(Optional.toOptional(getElementsOrNull()))
        }
    }

    private fun storeAllOnCacheAndPublish(modelList: List<Value>) {
        val modelMap = HashMap<Key, Value>()
        modelList.forEach { model -> modelMap[extractKeyFromValue.invoke(model)] = model }
        cache.putAll(modelMap)
        synchronized(singleSubjectsMap) {
            modelMap.entries.forEach { modelEntry ->
                singleSubjectsMap[modelEntry.key]?.onNext(Optional.toOptional(modelEntry.value))
            }
        }
        allSubject.onNext(Optional.toOptional(getElementsOrNull()))
    }

    private fun getElementsOrNull(): List<Value>? =
        cache.elements().toList().ifEmpty { null }
}