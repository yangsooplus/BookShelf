package com.yangsooplus.bookshelf.feature.book.screen.search

import com.yangsooplus.bookshelf.core.mvi.Intent
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchSort

internal sealed interface BookSearchIntent : Intent {
    data class ChangeQuery(val query: String) : BookSearchIntent
    data class Search(val query: String) : BookSearchIntent
    data class ChangeSort(val sort: BookSearchSort) : BookSearchIntent
    data class ClickBook(val book: Book) : BookSearchIntent
    data class ToggleFavorite(val book: Book) : BookSearchIntent
    data object OpenSortPanel : BookSearchIntent
    data object CloseSortPanel : BookSearchIntent
    data object LoadMore : BookSearchIntent
    data object EnterScreen : BookSearchIntent
    data object Retry : BookSearchIntent
}
