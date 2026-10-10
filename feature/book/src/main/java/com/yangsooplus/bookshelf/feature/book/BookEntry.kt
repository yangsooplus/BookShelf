package com.yangsooplus.bookshelf.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yangsooplus.bookshelf.feature.book.navigation.BookNavigationHost
import com.yangsooplus.bookshelf.feature.book.navigation.rememberBookNavigationState

@Composable
fun BookEntry(modifier: Modifier = Modifier) {
    BookNavigationHost(navigation = rememberBookNavigationState(), modifier = modifier)
}
