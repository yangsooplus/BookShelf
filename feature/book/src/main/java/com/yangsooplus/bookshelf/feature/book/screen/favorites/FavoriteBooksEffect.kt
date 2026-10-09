package com.yangsooplus.bookshelf.feature.book.screen.favorites

import com.yangsooplus.bookshelf.core.mvi.Effect
import com.yangsooplus.bookshelf.domain.book.model.Book

internal sealed interface FavoriteBooksEffect : Effect {
    sealed interface Navigation : FavoriteBooksEffect {
        data class OpenDetail(val book: Book) : Navigation
        data object OpenSearch : Navigation
    }
    data class ShowMessage(val message: String) : FavoriteBooksEffect
}
