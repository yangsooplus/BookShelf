package com.yangsooplus.bookshelf.feature.book.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.yangsooplus.bookshelf.domain.book.model.Book

@Composable
internal fun BookDetailScreen(book: Book, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().testTag("book-detail"))
}
