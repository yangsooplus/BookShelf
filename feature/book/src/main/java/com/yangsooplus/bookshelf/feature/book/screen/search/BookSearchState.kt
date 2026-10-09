package com.yangsooplus.bookshelf.feature.book.screen.search

import com.yangsooplus.bookshelf.core.mvi.State
import com.yangsooplus.bookshelf.domain.book.model.Book

internal data class BookSearchState(
    val query: String = "",
    val searchedQuery: String = "",
    val page: Int = 0,
    val books: List<Book> = emptyList(),
    val status: BookSearchStatus = BookSearchStatus.Initial,
    val sort: BookSearchSort = BookSearchSort.Accuracy,
    val isSortPanelVisible: Boolean = false,
    val pageStatus: BookSearchPageStatus = BookSearchPageStatus.MoreAvailable,
) : State {
    enum class BookSearchStatus { Initial, Loading, Results, Empty, Error }
    enum class BookSearchPageStatus { MoreAvailable, Loading, Error, End }
    enum class BookSearchSort(val label: String) { Accuracy("정확도순"), PublishedDate("발간일순") }
}
