package com.yangsooplus.bookshelf.feature.book.screen.search

import com.yangsooplus.bookshelf.core.mvi.Reducer
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchPageStatus
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchSort
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchStatus

internal sealed class BookSearchReducer(
    reducer: Reducer<BookSearchState>,
) : Reducer<BookSearchState> by reducer {
    data class UpdateQuery(val query: String) : BookSearchReducer(
        reducer = { state -> state.copy(query = query) },
    )

    data object ShowSortPanel : BookSearchReducer(
        reducer = { state -> state.copy(isSortPanelVisible = true) },
    )

    data object HideSortPanel : BookSearchReducer(
        reducer = { state -> state.copy(isSortPanelVisible = false) },
    )

    data class UpdateSort(val sort: BookSearchSort) : BookSearchReducer(
        reducer = { state -> state.copy(sort = sort) },
    )

    data class StartSearch(val query: String, val sort: BookSearchSort) : BookSearchReducer(
        reducer = { state ->
            state.copy(
                searchedQuery = query,
                sort = sort,
                status = BookSearchStatus.Loading,
                pageStatus = BookSearchPageStatus.MoreAvailable,
                page = 0,
            )
        },
    )

    data object ClearBooks : BookSearchReducer(
        reducer = { state -> state.copy(books = emptyList()) },
    )

    data object ResetSearchResults : BookSearchReducer(
        reducer = { state -> BookSearchState(query = state.query, sort = state.sort) },
    )

    data object ShowPageLoading : BookSearchReducer(
        reducer = { state -> state.copy(pageStatus = BookSearchPageStatus.Loading) },
    )

    data class UpdateBooks(val books: List<Book>, val page: Int) : BookSearchReducer(
        reducer = { state ->
            state.copy(
                books = (state.books + books).distinctBy { it.id },
                status = BookSearchStatus.Results,
                pageStatus = BookSearchPageStatus.MoreAvailable,
                page = page,
            )
        },
    )

    data object ShowEmptyResults : BookSearchReducer(
        reducer = { state ->
            state.copy(
                books = emptyList(),
                status = BookSearchStatus.Empty,
                pageStatus = BookSearchPageStatus.End,
            )
        },
    )

    data object MarkEndOfResults : BookSearchReducer(
        reducer = { state -> state.copy(pageStatus = BookSearchPageStatus.End) },
    )

    data class ShowLoadError(val isNextPage: Boolean) : BookSearchReducer(
        reducer = { state ->
            if (isNextPage) state.copy(pageStatus = BookSearchPageStatus.Error)
            else state.copy(status = BookSearchStatus.Error)
        },
    )

    data class UpdateFavorite(val book: Book) : BookSearchReducer(
        reducer = { state ->
            state.copy(
                books = state.books.map { currentBook ->
                    if (currentBook.id == book.id) currentBook.copy(isFavorite = book.isFavorite) else currentBook
                },
            )
        },
    )
}
