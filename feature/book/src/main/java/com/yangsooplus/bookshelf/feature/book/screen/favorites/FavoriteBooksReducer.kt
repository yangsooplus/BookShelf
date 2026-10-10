package com.yangsooplus.bookshelf.feature.book.screen.favorites

import com.yangsooplus.bookshelf.core.mvi.Reducer
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksStatus
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksPageStatus

internal sealed class FavoriteBooksReducer(
    reducer: Reducer<FavoriteBooksState>,
) : Reducer<FavoriteBooksState> by reducer {
    data class UpdateQuery(val query: String) : FavoriteBooksReducer(
        reducer = { state -> state.copy(query = query) },
    )
    data class UpdateSort(val sort: FavoriteBooksSort) : FavoriteBooksReducer(
        reducer = { state -> state.copy(sort = sort) },
    )
    data class UpdatePriceFilter(val minPrice: Int?, val maxPrice: Int?) : FavoriteBooksReducer(
        reducer = { state -> state.copy(minPriceFilter = minPrice, maxPriceFilter = maxPrice) },
    )
    data class UpdateMetaData(val totalCount: Int, val maxPrice: Int?) : FavoriteBooksReducer(
        reducer = { state -> state.copy(totalCount = totalCount, maxPrice = maxPrice) },
    )
    data object ShowSortPanel : FavoriteBooksReducer(reducer = { it.copy(isSortPanelVisible = true) })
    data object HideSortPanel : FavoriteBooksReducer(reducer = { it.copy(isSortPanelVisible = false) })
    data object ShowPricePanel : FavoriteBooksReducer(reducer = { it.copy(isPricePanelVisible = true) })
    data object HidePricePanel : FavoriteBooksReducer(reducer = { it.copy(isPricePanelVisible = false) })
    data class StartSearch(val query: String) : FavoriteBooksReducer(
        reducer = { state -> state.copy(
            searchedQuery = query,
            books = emptyList(),
            page = 0,
            status = FavoriteBooksStatus.Loading,
            pageStatus = FavoriteBooksPageStatus.MoreAvailable,
        ) },
    )
    data class UpdateBooks(val books: List<Book>, val page: Int) : FavoriteBooksReducer(
        reducer = { state -> state.copy(
            books = (state.books + books).distinctBy { it.id },
            page = page,
            status = FavoriteBooksStatus.Results,
            pageStatus = FavoriteBooksPageStatus.MoreAvailable,
        ) },
    )
    data object ShowEmptyResults : FavoriteBooksReducer(
        reducer = { it.copy(books = emptyList(), status = FavoriteBooksStatus.Empty, pageStatus = FavoriteBooksPageStatus.End) },
    )
    data object ShowPageLoading : FavoriteBooksReducer(reducer = { it.copy(pageStatus = FavoriteBooksPageStatus.Loading) })
    data object MarkEndOfResults : FavoriteBooksReducer(reducer = { it.copy(pageStatus = FavoriteBooksPageStatus.End) })
    data class ShowLoadError(val isNextPage: Boolean) : FavoriteBooksReducer(
        reducer = { state ->
            if (isNextPage) state.copy(pageStatus = FavoriteBooksPageStatus.Error)
            else state.copy(status = FavoriteBooksStatus.Error)
        },
    )
    data class RemoveBook(val book: Book) : FavoriteBooksReducer(
        reducer = { state -> state.copy(books = state.books.filterNot { it.id == book.id }) },
    )
}
