package com.yangsooplus.bookshelf.feature.book.screen.detail

import com.yangsooplus.bookshelf.core.mvi.Effect
import com.yangsooplus.bookshelf.domain.book.model.Book

internal sealed interface BookDetailEffect : Effect {
    data object GoBack : BookDetailEffect
    data class UpdateFavorite(val book: Book) : BookDetailEffect
    data class ShowMessage(val message: String) : BookDetailEffect
}
