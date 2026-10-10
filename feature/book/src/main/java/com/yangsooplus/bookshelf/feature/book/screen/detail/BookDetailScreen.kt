package com.yangsooplus.bookshelf.feature.book.screen.detail

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
import com.yangsooplus.bookshelf.feature.book.screen.detail.component.BookDetailContent

@Composable
internal fun BookDetailScreen(
    book: Book,
    onBack: () -> Unit,
    onFavoriteChanged: (Book) -> Unit,
    favoriteUpdate: Book? = null,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnBack by rememberUpdatedState(newValue = onBack)
    val currentOnFavoriteChanged by rememberUpdatedState(newValue = onFavoriteChanged)
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                BookDetailEffect.GoBack -> currentOnBack()
                is BookDetailEffect.UpdateFavorite -> currentOnFavoriteChanged(effect.book)
                is BookDetailEffect.ShowMessage -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    LaunchedEffect(key1 = viewModel, key2 = book) {
        viewModel.intent(intent = BookDetailIntent.Init(book = book))
    }
    LaunchedEffect(key1 = favoriteUpdate) {
        favoriteUpdate?.let { updatedBook ->
            viewModel.intent(intent = BookDetailIntent.UpdateFavorite(book = updatedBook))
        }
    }
    state.book?.let { currentBook ->
        BookDetailContent(
            book = currentBook,
            onBack = { viewModel.intent(intent = BookDetailIntent.GoBack) },
            onFavoriteChange = { viewModel.intent(intent = BookDetailIntent.ToggleFavorite) },
            modifier = modifier.testTag(tag = "book-detail"),
        )
    }
}
