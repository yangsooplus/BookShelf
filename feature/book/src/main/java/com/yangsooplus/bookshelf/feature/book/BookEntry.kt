package com.yangsooplus.bookshelf.feature.book

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yangsooplus.bookshelf.core.designsystem.theme.BookShelfTheme

/** Public entry point. Screens and state implementations remain internal to this module. */
@Composable
fun BookEntry(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        BookScreen(Modifier.padding(innerPadding))
    }
}

@Composable
internal fun BookScreen(modifier: Modifier = Modifier) {
    Text(text = "Hello Android!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun BookEntryPreview() {
    BookShelfTheme { BookEntry() }
}
