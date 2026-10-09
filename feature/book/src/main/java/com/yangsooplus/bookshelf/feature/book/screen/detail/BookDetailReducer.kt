package com.yangsooplus.bookshelf.feature.book.screen.detail

import com.yangsooplus.bookshelf.core.mvi.Reducer
import com.yangsooplus.bookshelf.domain.book.model.Book

internal sealed class BookDetailReducer(
    reducer: Reducer<BookDetailState>,
) : Reducer<BookDetailState> by reducer {
    data class UpdateBook(val book: Book) : BookDetailReducer(
        reducer = { state -> state.copy(book = book) },
    )
}
