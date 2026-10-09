package com.yangsooplus.bookshelf.feature.book.screen.favorites

import com.yangsooplus.bookshelf.core.mvi.State
import com.yangsooplus.bookshelf.domain.book.model.Book

internal data class FavoriteBooksState(
    val query: String = "",
    val searchedQuery: String = "",
    val books: List<Book> = emptyList(),
    val page: Int = 0,
    val totalCount: Int = 0,
    val maxPrice: Int? = null,
    val minPriceFilter: Int? = null,
    val maxPriceFilter: Int? = null,
    val sort: FavoriteBooksSort = FavoriteBooksSort.TitleAscending,
    val status: FavoriteBooksStatus = FavoriteBooksStatus.Loading,
    val pageStatus: FavoriteBooksPageStatus = FavoriteBooksPageStatus.MoreAvailable,
    val isSortPanelVisible: Boolean = false,
    val isPricePanelVisible: Boolean = false,
) : State {
    val priceRangeUpperBound: Int
        get() = (((maxPrice ?: 0).toLong() + 9_999) / 10_000 * 10_000)
            .coerceIn(minimumValue = 10_000L, maximumValue = Int.MAX_VALUE.toLong()).toInt()

    enum class FavoriteBooksStatus { Loading, Results, Empty, Error }
    enum class FavoriteBooksPageStatus { MoreAvailable, Loading, Error, End }
    enum class FavoriteBooksSort(val label: String) {
        TitleAscending("제목 오름차순"), TitleDescending("제목 내림차순")
    }
}
