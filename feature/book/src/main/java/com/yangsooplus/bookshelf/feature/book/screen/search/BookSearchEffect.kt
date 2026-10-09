package com.yangsooplus.bookshelf.feature.book.screen.search

import com.yangsooplus.bookshelf.core.mvi.Effect
import com.yangsooplus.bookshelf.domain.book.model.Book

internal sealed interface BookSearchEffect : Effect {
    data class UpdateFavorite(val book: Book) : BookSearchEffect
    data class ShowMessage(val message: String) : BookSearchEffect

    sealed interface Navigation : BookSearchEffect {
        data class OpenDetail(val book: Book) : Navigation
    }
}
