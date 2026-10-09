package com.yangsooplus.bookshelf.feature.book.screen.favorites

import com.yangsooplus.bookshelf.core.mvi.Intent
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort

internal sealed interface FavoriteBooksIntent : Intent {
    data class ChangeQuery(val query: String) : FavoriteBooksIntent
    data class Search(val query: String) : FavoriteBooksIntent
    data class ChangeSort(val sort: FavoriteBooksSort) : FavoriteBooksIntent
    data class ApplyPriceFilter(val minPrice: Int, val maxPrice: Int) : FavoriteBooksIntent
    data class ClickBook(val book: Book) : FavoriteBooksIntent
    data class RemoveFavorite(val book: Book) : FavoriteBooksIntent
    data object ResetPriceFilter : FavoriteBooksIntent
    data object OpenSortPanel : FavoriteBooksIntent
    data object CloseSortPanel : FavoriteBooksIntent
    data object OpenPricePanel : FavoriteBooksIntent
    data object ClosePricePanel : FavoriteBooksIntent
    data object LoadMore : FavoriteBooksIntent
    data object EnterScreen : FavoriteBooksIntent
    data object Retry : FavoriteBooksIntent
    data object OpenSearch : FavoriteBooksIntent
}
