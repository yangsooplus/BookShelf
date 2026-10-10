package com.yangsooplus.bookshelf.feature.book.screen.detail

import com.yangsooplus.bookshelf.core.mvi.Intent
import com.yangsooplus.bookshelf.domain.book.model.Book

internal sealed interface BookDetailIntent : Intent {
    data class Init(val book: Book) : BookDetailIntent
    data class UpdateFavorite(val book: Book) : BookDetailIntent
    data object ToggleFavorite : BookDetailIntent
    data object GoBack : BookDetailIntent
}
