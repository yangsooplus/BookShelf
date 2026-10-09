package com.yangsooplus.bookshelf.feature.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

/** Public entry point. Screens and state implementations remain internal to this module. */
@Composable
fun BookEntry(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BSTheme.colors.backgroundSecondary)
            .safeDrawingPadding(),
    ) {
        BookScreen()
    }
}

@Composable
internal fun BookScreen(modifier: Modifier = Modifier) {
    BasicText(
        text = "Hello Android!",
        modifier = modifier,
        style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.contentPrimary),
    )
}

@Preview(showBackground = true)
@Composable
private fun BookEntryPreview() {
    BSTheme { BookEntry() }
}
