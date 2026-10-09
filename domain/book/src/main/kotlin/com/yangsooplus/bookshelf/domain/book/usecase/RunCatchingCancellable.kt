package com.yangsooplus.bookshelf.domain.book.usecase

import kotlinx.coroutines.CancellationException

/** 취소는 재전파하고, 일반 예외만 [onFailure]에 전달한다. */
internal inline fun <T> runCatchingCancellable(
    onFailure: (Exception) -> T,
    block: () -> T,
): T = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    onFailure(e)
}
