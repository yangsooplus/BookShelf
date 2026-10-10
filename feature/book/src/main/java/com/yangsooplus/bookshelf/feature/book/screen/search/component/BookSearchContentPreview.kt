package com.yangsooplus.bookshelf.feature.book.screen.search.component

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
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchPageStatus
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchSort
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchStatus
import java.time.LocalDate

private data class BookSearchPreviewCase(
    val status: BookSearchStatus,
    val pageStatus: BookSearchPageStatus = BookSearchPageStatus.MoreAvailable,
    val books: List<Book> = previewSearchBooks,
)

private class BookSearchPreviewProvider : PreviewParameterProvider<BookSearchPreviewCase> {
    override val values = sequenceOf(
        BookSearchPreviewCase(status = BookSearchStatus.Initial),
        BookSearchPreviewCase(status = BookSearchStatus.Results),
        BookSearchPreviewCase(status = BookSearchStatus.Loading),
        BookSearchPreviewCase(status = BookSearchStatus.Empty),
        BookSearchPreviewCase(status = BookSearchStatus.Error),
        BookSearchPreviewCase(
            status = BookSearchStatus.Results,
            pageStatus = BookSearchPageStatus.Loading,
        ),
        BookSearchPreviewCase(
            status = BookSearchStatus.Results,
            pageStatus = BookSearchPageStatus.Error,
        ),
        BookSearchPreviewCase(
            status = BookSearchStatus.Results,
            pageStatus = BookSearchPageStatus.End,
        ),
        BookSearchPreviewCase(
            status = BookSearchStatus.Results,
            books = listOf(
                previewSearchBooks.first().copy(
                    title = "아주 긴 제목과 여러 저자가 있는 도서도 필요한 정보를 읽을 수 있도록 표시합니다",
                    authors = listOf("김서윤", "박지호", "이도현"),
                    publisher = "이름이 긴 출판사",
                    publishedDate = LocalDate.MIN,
                    price = BookPrice(regularPrice = -1),
                ),
                previewSearchBooks[1].copy(price = BookPrice(regularPrice = 0)),
            ),
        ),
    )
}

@Preview(name = "검색 · 상태별", showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun BookSearchContentPreview(
    @PreviewParameter(BookSearchPreviewProvider::class) case: BookSearchPreviewCase,
) = BSTheme { BookSearchPreviewContent(case = case) }

@Preview(name = "검색 · 태블릿 2열", showBackground = true, widthDp = 688, heightDp = 928)
@Composable
private fun BookSearchTabletPreview() = BSTheme {
    BookSearchPreviewContent(
        case = BookSearchPreviewCase(status = BookSearchStatus.Results),
        columns = 2,
    )
}

@Preview(name = "검색 · 큰 글자", showBackground = true, widthDp = 390, heightDp = 720, fontScale = 1.5f)
@Composable
private fun BookSearchLargeFontPreview() = BSTheme {
    BookSearchPreviewContent(case = BookSearchPreviewCase(status = BookSearchStatus.Results))
}

@Composable
private fun BookSearchPreviewContent(case: BookSearchPreviewCase, columns: Int = 1) {
    var query by remember { mutableStateOf(value = if (case.status == BookSearchStatus.Initial) "" else "기록") }
    var sort by remember { mutableStateOf(value = BookSearchSort.Accuracy) }
    var status by remember { mutableStateOf(value = case.status) }
    var isSortPanelVisible by remember { mutableStateOf(value = false) }
    var books by remember { mutableStateOf(value = case.books) }
    BookSearchContent(
        query = query,
        books = books,
        status = status,
        sort = sort,
        pageStatus = case.pageStatus,
        isSortPanelVisible = isSortPanelVisible,
        onQueryChange = { query = it },
        onSearch = { status = if (it.isBlank()) BookSearchStatus.Initial else BookSearchStatus.Results },
        onSortChange = {
            sort = it
            isSortPanelVisible = false
        },
        onOpenSortPanel = { isSortPanelVisible = true },
        onCloseSortPanel = { isSortPanelVisible = false },
        onBookClick = {},
        onFavoriteChange = { selectedBook, isFavorite ->
            books = books.map { book ->
                if (book.id == selectedBook.id) book.copy(isFavorite = isFavorite) else book
            }
        },
        onLoadMore = {},
        onRetry = { status = BookSearchStatus.Results },
        columns = columns,
    )
}

private val previewSearchBooks = listOf(
    previewBook(
        id = "9780000000001",
        title = "생각의 기록",
        author = "김서윤",
        publisher = "문장숲",
        date = LocalDate.of(2026, 9, 1),
        price = 14_400,
        favorite = true,
    ),
    previewBook(
        id = "9780000000002",
        title = "오늘의 문장",
        author = "박지호",
        publisher = "하루책",
        date = LocalDate.of(2026, 8, 14),
        price = 12_600,
    ),
    previewBook(
        id = "9780000000003",
        title = "작은 책방",
        author = "이도현",
        publisher = "책의자리",
        date = LocalDate.of(2026, 7, 20),
        price = 16_200,
    ),
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
    price = BookPrice(regularPrice = price),
    thumbnailUrl = "",
    isbn = id,
    translators = emptyList(),
    isFavorite = favorite,
)
