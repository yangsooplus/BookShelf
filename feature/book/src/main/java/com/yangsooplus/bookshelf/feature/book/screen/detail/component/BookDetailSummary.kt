package com.yangsooplus.bookshelf.feature.book.screen.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.component.BookCover
import com.yangsooplus.bookshelf.feature.book.util.formatAuthorPublisher
import com.yangsooplus.bookshelf.feature.book.util.formatPublishedDate
import com.yangsooplus.bookshelf.feature.book.util.formatPrice
import com.yangsooplus.bookshelf.feature.book.util.formatRegularPrice
import com.yangsooplus.bookshelf.feature.book.util.formatSalePriceCaption

@Composable
internal fun BookDetailSummary(book: Book, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(space = 20.dp)) {
        BookCover(
            thumbnailUrl = book.thumbnailUrl,
            modifier = Modifier.size(width = 120.dp, height = 168.dp)
        )
        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            BasicText(
                text = book.formatAuthorPublisher(),
                style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.contentPrimary)
            )
            BasicText(
                text = book.formatPublishedDate(),
                style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.supportNormal)
            )
            BasicText(
                text = book.formatRegularPrice(),
                style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.supportNormal)
            )
            BasicText(
                text = book.formatPrice(),
                style = BSTheme.typography.displaySmall.copy(color = BSTheme.colors.contentPrimary)
            )
            book.formatSalePriceCaption()?.let { caption ->
                BasicText(
                    text = caption,
                    style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.contentInteractive)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 350)
@Composable
private fun BookDetailSummaryPreview() = BSTheme {
    BookDetailSummary(book = bookDetailPreviewBook)
}
