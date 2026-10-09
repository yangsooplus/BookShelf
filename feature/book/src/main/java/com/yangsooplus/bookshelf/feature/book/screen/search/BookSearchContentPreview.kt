package com.yangsooplus.bookshelf.feature.book.screen.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.time.LocalDate

internal data class BookSearchPreviewCase(
    val status: BookSearchStatus,
    val pageStatus: BookSearchPageStatus = BookSearchPageStatus.MoreAvailable,
    val books: List<Book> = previewSearchBooks,
)

internal class BookSearchPreviewProvider : PreviewParameterProvider<BookSearchPreviewCase> {
    override val values = sequenceOf(
        BookSearchPreviewCase(BookSearchStatus.Initial),
        BookSearchPreviewCase(BookSearchStatus.Results),
        BookSearchPreviewCase(BookSearchStatus.Loading),
        BookSearchPreviewCase(BookSearchStatus.Empty),
        BookSearchPreviewCase(BookSearchStatus.Error),
        BookSearchPreviewCase(BookSearchStatus.Results, BookSearchPageStatus.Loading),
        BookSearchPreviewCase(BookSearchStatus.Results, BookSearchPageStatus.Error),
        BookSearchPreviewCase(BookSearchStatus.Results, BookSearchPageStatus.End),
        BookSearchPreviewCase(BookSearchStatus.Results, books = listOf(
            previewSearchBooks.first().copy(
                title = "아주 긴 제목과 여러 저자가 있는 도서도 필요한 정보를 읽을 수 있도록 표시합니다",
                authors = listOf("김서윤", "박지호", "이도현"),
                publisher = "이름이 긴 출판사",
                publishedDate = LocalDate.MIN,
                price = BookPrice(-1),
            ),
            previewSearchBooks[1].copy(price = BookPrice(0)),
        )),
    )
}

@Preview(name = "검색 · 상태별", showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun BookSearchContentPreview(
    @PreviewParameter(BookSearchPreviewProvider::class) case: BookSearchPreviewCase,
) = BSTheme { BookSearchPreviewContent(case) }

@Preview(name = "검색 · 태블릿 2열", showBackground = true, widthDp = 688, heightDp = 928)
@Composable
private fun BookSearchTabletPreview() = BSTheme {
    BookSearchPreviewContent(BookSearchPreviewCase(BookSearchStatus.Results), columns = 2)
}

@Preview(name = "검색 · 큰 글자", showBackground = true, widthDp = 390, heightDp = 720, fontScale = 1.5f)
@Composable
private fun BookSearchLargeFontPreview() = BSTheme {
    BookSearchPreviewContent(BookSearchPreviewCase(BookSearchStatus.Results))
}

@Composable
internal fun BookSearchPreviewContent(case: BookSearchPreviewCase, columns: Int = 1) {
    var query by remember { mutableStateOf(if (case.status == BookSearchStatus.Initial) "" else "기록") }
    var sort by remember { mutableStateOf(BookSearchSort.Accuracy) }
    var status by remember { mutableStateOf(case.status) }
    var books by remember { mutableStateOf(case.books) }
    BookSearchContent(
        query = query,
        books = books,
        status = status,
        sort = sort,
        pageStatus = case.pageStatus,
        onQueryChange = { query = it },
        onSearch = { status = if (query.isBlank()) BookSearchStatus.Initial else BookSearchStatus.Results },
        onSortChange = { sort = it },
        onBookClick = {},
        onFavoriteChange = { selected, favorite ->
            books = books.map { if (it.id == selected.id) it.copy(isFavorite = favorite) else it }
        },
        onLoadMore = {},
        onRetry = { status = BookSearchStatus.Results },
        columns = columns,
        useSortDialog = columns > 1,
    )
}

private val previewSearchBooks = listOf(
    previewBook("9780000000001", "생각의 기록", "김서윤", "문장숲", LocalDate.of(2026, 9, 1), 14_400, true),
    previewBook("9780000000002", "오늘의 문장", "박지호", "하루책", LocalDate.of(2026, 8, 14), 12_600),
    previewBook("9780000000003", "작은 책방", "이도현", "책의자리", LocalDate.of(2026, 7, 20), 16_200),
)

private fun previewBook(
    id: String,
    title: String,
    author: String,
    publisher: String,
    date: LocalDate,
    price: Int,
    favorite: Boolean = false,
) = Book(
    id = id,
    title = title,
    contents = "",
    url = "",
    authors = listOf(author),
    publisher = publisher,
    publishedDate = date,
    price = BookPrice(price),
    thumbnailUrl = "",
    isbn = id,
    translators = emptyList(),
    isFavorite = favorite,
)
