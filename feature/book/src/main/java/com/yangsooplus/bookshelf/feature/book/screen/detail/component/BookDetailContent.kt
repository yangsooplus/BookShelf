package com.yangsooplus.bookshelf.feature.book.screen.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSButtonStyle
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import com.yangsooplus.bookshelf.feature.book.util.formatContents
import com.yangsooplus.bookshelf.feature.book.util.formatInformation
import java.time.LocalDate

@Composable
internal fun BookDetailContent(
    book: Book,
    onBack: () -> Unit,
    onFavoriteChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = BSTheme.colors.backgroundPrimary)
    ) {
        BookDetailTopBar(
            isFavorite = book.isFavorite,
            onBack = onBack,
            onFavoriteChange = onFavoriteChange
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .verticalScroll(state = rememberScrollState())
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        ) {
            BasicText(
                text = "도서 상세",
                style = BSTheme.typography.ui14.copy(color = BSTheme.colors.contentInteractive)
            )
            BasicText(
                text = book.title,
                style = BSTheme.typography.displayMedium.copy(color = BSTheme.colors.contentPrimary)
            )
            BookDetailSummary(book = book, modifier = Modifier.fillMaxWidth())
            BSButton(
                label = if (book.isFavorite) "즐겨찾기에 저장됨" else "즐겨찾기에 저장",
                onClick = onFavoriteChange,
                modifier = Modifier.fillMaxWidth(),
                style = if (book.isFavorite) BSButtonStyle.Secondary else BSButtonStyle.Primary,
            )
            BasicText(
                text = "책 소개",
                style = BSTheme.typography.titleLarge.copy(color = BSTheme.colors.contentPrimary)
            )
            BasicText(
                text = book.formatContents(),
                style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.contentPrimary)
            )
            BookDetailInformation(text = book.formatInformation())
        }
    }
}

@Composable
private fun BookDetailInformation(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = BSTheme.colors.backgroundSecondary,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        BasicText(
            text = "도서 정보",
            style = BSTheme.typography.titleMedium.copy(color = BSTheme.colors.contentPrimary)
        )
        BasicText(
            text = text,
            style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.supportNormal)
        )
    }
}

internal val bookDetailPreviewBook = Book(
    id = "9780000000001",
    title = "생각의 기록",
    contents = "흘려보내기 쉬운 순간을 기록하고, 일상을 다시 발견하는 방법을 담았습니다.",
    url = "",
    authors = listOf("김서윤"),
    publisher = "문장숲",
    publishedDate = LocalDate.of(2026, 9, 1),
    price = BookPrice(regularPrice = 16_000, salePrice = 14_400),
    thumbnailUrl = "",
    isbn = "9780000000001",
    translators = emptyList(),
    isFavorite = true,
)

@Preview(showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun BookDetailContentPreview() = BSTheme {
    BookDetailContent(book = bookDetailPreviewBook, onBack = {}, onFavoriteChange = {})
}

@Preview(showBackground = true, widthDp = 350)
@Composable
private fun BookDetailInformationPreview() = BSTheme {
    BookDetailInformation(text = bookDetailPreviewBook.formatInformation())
}
