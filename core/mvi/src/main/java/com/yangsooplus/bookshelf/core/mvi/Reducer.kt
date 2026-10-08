package com.yangsooplus.bookshelf.core.mvi

/**
 * A named, pure state transformation.
 *
 * Define a sealed reducer interface for each screen and implement [reduce] directly
 * in its data classes or data objects. May be evaluated more than once during
 * concurrent state updates, so implementations must not perform side effects.
 */
interface Reducer<S : State> {
    fun reduce(state: S): S
}
