package com.yangsooplus.bookshelf.feature.book.screen.favorites

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.screen.favorites.component.FavoriteBooksContent

@Composable
internal fun FavoriteBooksScreen(
    onBookClick: (Book) -> Unit,
    onSearchClick: () -> Unit,
    favoriteUpdate: Book? = null,
    onFavoriteChanged: (Book) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FavoriteBooksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnBookClick by rememberUpdatedState(newValue = onBookClick)
    val currentOnSearchClick by rememberUpdatedState(newValue = onSearchClick)
    val currentOnFavoriteChanged by rememberUpdatedState(newValue = onFavoriteChanged)
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteBooksEffect.Navigation.OpenDetail -> currentOnBookClick(effect.book)
                FavoriteBooksEffect.Navigation.OpenSearch -> currentOnSearchClick()
                is FavoriteBooksEffect.UpdateFavorite -> currentOnFavoriteChanged(effect.book)
                is FavoriteBooksEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(key1 = viewModel, key2 = favoriteUpdate) {
        viewModel.intent(intent = FavoriteBooksIntent.EnterScreen)
    }
    FavoriteBooksContent(
        query = state.query,
        books = state.books,
        status = state.status,
        sort = state.sort,
        pageStatus = state.pageStatus,
        totalCount = state.totalCount,
        minPriceFilter = state.minPriceFilter,
        maxPriceFilter = state.maxPriceFilter,
        priceRangeUpperBound = state.priceRangeUpperBound,
        isPriceFilterEnabled = state.maxPrice != null,
        isSortPanelVisible = state.isSortPanelVisible,
        isPricePanelVisible = state.isPricePanelVisible,
        onOpenPricePanel = { viewModel.intent(intent = FavoriteBooksIntent.OpenPricePanel) },
        onClosePricePanel = { viewModel.intent(intent = FavoriteBooksIntent.ClosePricePanel) },
        onApplyPriceFilter = { minPrice, maxPrice ->
            viewModel.intent(intent = FavoriteBooksIntent.ApplyPriceFilter(minPrice = minPrice, maxPrice = maxPrice))
        },
        onResetPriceFilter = { viewModel.intent(intent = FavoriteBooksIntent.ResetPriceFilter) },
        onSearchClick = { viewModel.intent(intent = FavoriteBooksIntent.OpenSearch) },
        onQueryChange = { viewModel.intent(intent = FavoriteBooksIntent.ChangeQuery(query = it)) },
        onSearch = { viewModel.intent(intent = FavoriteBooksIntent.Search(query = it)) },
        onSortChange = { viewModel.intent(intent = FavoriteBooksIntent.ChangeSort(sort = it)) },
        onOpenSortPanel = { viewModel.intent(intent = FavoriteBooksIntent.OpenSortPanel) },
        onCloseSortPanel = { viewModel.intent(intent = FavoriteBooksIntent.CloseSortPanel) },
        onBookClick = { viewModel.intent(intent = FavoriteBooksIntent.ClickBook(book = it)) },
        onFavoriteChange = { book, _ ->
            viewModel.intent(intent = FavoriteBooksIntent.RemoveFavorite(book = book))
        },
        onLoadMore = { viewModel.intent(intent = FavoriteBooksIntent.LoadMore) },
        onRetry = { viewModel.intent(intent = FavoriteBooksIntent.Retry) },
        modifier = modifier.testTag(tag = "book-favorites"),
    )
}
