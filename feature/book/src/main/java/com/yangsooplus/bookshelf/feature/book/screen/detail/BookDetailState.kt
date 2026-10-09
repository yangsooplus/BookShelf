package com.yangsooplus.bookshelf.feature.book.screen.detail

import com.yangsooplus.bookshelf.core.mvi.State
import com.yangsooplus.bookshelf.domain.book.model.Book

internal data class BookDetailState(val book: Book? = null) : State
