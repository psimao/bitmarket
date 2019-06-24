package com.psimao.bitmarket.domain

import com.gojuno.koptional.Optional
import com.psimao.bitmarket.domain.transformer.UnwrapOptionalTransformer
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class UnwrapOptionalTransformerTest {

    private lateinit var unwrapOptionalTransformer: UnwrapOptionalTransformer<String>

    @Before
    fun setup() {
        unwrapOptionalTransformer = UnwrapOptionalTransformer()
    }

    @Test
    fun `Given that the optional is none, when the transformation happens, then no values should be emitted`() {
        val none = Optional.toOptional(null)
        Flowable.just(none)
            .compose(unwrapOptionalTransformer)
            .test()
            .assertNoValues()
    }

    @Test
    fun `Given that the optional is some, when the transformation happens, then the value should be emitted`() {
        val someValue = "some"
        val some = Optional.toOptional(someValue)
        Flowable.just(some)
            .compose(unwrapOptionalTransformer)
            .test()
            .assertValue(someValue)
    }
}