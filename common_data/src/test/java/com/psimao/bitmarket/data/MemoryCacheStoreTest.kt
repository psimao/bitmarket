package com.psimao.bitmarket.data

import com.gojuno.koptional.Optional
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class MemoryCacheStoreTest {

    private lateinit var reactiveStore: MemoryCacheStore<Long, StoredObject>

    @Before
    fun setup() {
        reactiveStore = MemoryCacheStore { it.id }
    }

    @Test
    fun `Given that the store has no items, when a subscription happens, then none should be emitted`() {
        val none = Optional.toOptional(null)
        reactiveStore.getAll()
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .test()
            .assertValue(none)
    }

    @Test
    fun `Given that the store has items, when a single subscription happens, then the related item should be emitted`() {
        val id: Long = 1
        val obj = StoredObject(id)
        val some = Optional.toOptional(obj)
        reactiveStore.store(obj)
        reactiveStore.get(id)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .test()
            .assertValue(some)
    }

    @Test
    fun `Given that the store has items, when a subscription happens, then all items should be emitted`() {
        val id: Long = 1
        val obj = StoredObject(id)
        val some = Optional.toOptional(listOf(obj))
        reactiveStore.store(obj)
        reactiveStore.getAll()
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .test()
            .assertValue(some)
    }

    data class StoredObject(val id: Long)
}