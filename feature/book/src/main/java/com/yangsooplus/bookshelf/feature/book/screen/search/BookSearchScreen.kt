package com.yangsooplus.bookshelf.feature.book.screen.search

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
import com.yangsooplus.bookshelf.feature.book.screen.search.component.BookSearchContent

@Composable
internal fun BookSearchScreen(
    onBookClick: (Book) -> Unit,
    favoriteUpdate: Book? = null,
    onFavoriteChanged: (Book) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BookSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnBookClick by rememberUpdatedState(newValue = onBookClick)
    val currentOnFavoriteChanged by rememberUpdatedState(newValue = onFavoriteChanged)
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookSearchEffect.Navigation.OpenDetail -> currentOnBookClick(effect.book)
                is BookSearchEffect.UpdateFavorite -> currentOnFavoriteChanged(effect.book)
                is BookSearchEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(key1 = viewModel) {
        viewModel.intent(intent = BookSearchIntent.EnterScreen)
    }
    LaunchedEffect(key1 = favoriteUpdate) {
        favoriteUpdate?.let { updatedBook ->
            viewModel.intent(intent = BookSearchIntent.UpdateFavorite(book = updatedBook))
        }
    }
    BookSearchContent(
        query = state.query,
        books = state.books,
        status = state.status,
        sort = state.sort,
        pageStatus = state.pageStatus,
        isSortPanelVisible = state.isSortPanelVisible,
        onQueryChange = { viewModel.intent(intent = BookSearchIntent.ChangeQuery(query = it)) },
        onSearch = { viewModel.intent(intent = BookSearchIntent.Search(query = it)) },
        onSortChange = { viewModel.intent(intent = BookSearchIntent.ChangeSort(sort = it)) },
        onOpenSortPanel = { viewModel.intent(intent = BookSearchIntent.OpenSortPanel) },
        onCloseSortPanel = { viewModel.intent(intent = BookSearchIntent.CloseSortPanel) },
        onBookClick = { viewModel.intent(intent = BookSearchIntent.ClickBook(book = it)) },
        onFavoriteChange = { book, _ ->
            viewModel.intent(intent = BookSearchIntent.ToggleFavorite(book = book))
        },
        onLoadMore = { viewModel.intent(intent = BookSearchIntent.LoadMore) },
        onRetry = { viewModel.intent(intent = BookSearchIntent.Retry) },
        modifier = modifier.testTag(tag = "book-search"),
    )
}
