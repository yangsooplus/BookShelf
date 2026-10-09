package com.yangsooplus.bookshelf.feature.book.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSButtonStyle
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.component.BSSearchBar
import com.yangsooplus.bookshelf.core.designsystem.component.BSStateMessage
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBanner
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBannerType
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.component.BookCard
import com.yangsooplus.bookshelf.feature.book.component.BookCardSkeleton

internal enum class BookSearchStatus { Initial, Loading, Results, Empty, Error }
internal enum class BookSearchPageStatus { MoreAvailable, Loading, Error, End }
internal enum class BookSearchSort(val label: String) { Accuracy("정확도순"), PublishedDate("발간일순") }

@Composable
internal fun BookSearchContent(
    query: String,
    books: List<Book>,
    status: BookSearchStatus,
    sort: BookSearchSort,
    pageStatus: BookSearchPageStatus,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSortChange: (BookSearchSort) -> Unit,
    onBookClick: (Book) -> Unit,
    onFavoriteChange: (Book, Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 1,
    useSortDialog: Boolean = false,
) {
    var sortPanelVisible by rememberSaveable { mutableStateOf(false) }

    Column(modifier.fillMaxSize().background(BSTheme.colors.backgroundSecondary)) {
        BookSearchTopBar()
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                BSSearchBar(query, onQueryChange, onSearch)
            }
            if (status == BookSearchStatus.Results || status == BookSearchStatus.Empty) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        BSButton(sort.label, { sortPanelVisible = true }, Modifier.width(106.dp), style = BSButtonStyle.Subtle)
                    }
                }
            }
            when (status) {
                BookSearchStatus.Initial -> item(span = { GridItemSpan(maxLineSpan) }) {
                    BSStateMessage("읽고 싶은 책을 찾아보세요", "제목이나 저자로 검색할 수 있어요.")
                }
                BookSearchStatus.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) { SearchCaption("책을 찾고 있어요…") }
                    items(3) { BookCardSkeleton() }
                }
                BookSearchStatus.Empty -> item(span = { GridItemSpan(maxLineSpan) }) {
                    BSStateMessage("검색 결과가 없어요", "다른 제목이나 저자로 다시 검색해보세요.") {
                        BSButton("검색어 지우기", { onQueryChange("") }, Modifier.fillMaxWidth())
                    }
                }
                BookSearchStatus.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    BSStateMessage("검색 결과를 불러오지 못했어요", "네트워크 연결을 확인한 뒤 다시 시도해주세요.", icon = BSIcon.Offline) {
                        BSButton("다시 시도", onRetry, Modifier.fillMaxWidth())
                    }
                }
                BookSearchStatus.Results -> {
                    if (pageStatus == BookSearchPageStatus.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                BSStatusBanner("다음 페이지를 불러오지 못했어요", type = BSStatusBannerType.Retry)
                                BSButton("다음 페이지 다시 시도", onLoadMore, Modifier.fillMaxWidth())
                                SearchCaption("기존 결과는 유지됩니다.")
                            }
                        }
                    }
                    items(books, key = { it.id }) { book ->
                        BookCard(book, { onBookClick(book) }, { onFavoriteChange(book, it) })
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        BookSearchPagination(pageStatus, onLoadMore)
                    }
                }
            }
        }
    }
    if (sortPanelVisible) {
        BookSearchSortPanel(
            sort = sort,
            useDialog = useSortDialog,
            onDismiss = { sortPanelVisible = false },
            onApply = {
                sortPanelVisible = false
                onSortChange(it)
            },
        )
    }
}

@Composable
private fun BookSearchTopBar() {
    Row(
        Modifier.fillMaxWidth().height(64.dp).background(BSTheme.colors.backgroundPrimary).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            "도서 검색",
            Modifier.weight(1f),
            style = BSTheme.typography.displaySmall.copy(color = BSTheme.colors.contentPrimary),
        )
        Box(Modifier.width(24.dp)) {
            Box(Modifier.size(8.dp, 24.dp).background(BSTheme.colors.buttonPrimary, RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
private fun BookSearchPagination(status: BookSearchPageStatus, onLoadMore: () -> Unit) {
    when (status) {
        BookSearchPageStatus.MoreAvailable -> BSButton("20개 더 보기", onLoadMore, Modifier.fillMaxWidth())
        BookSearchPageStatus.Loading -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BSButton("추가 20개 불러오는 중…", {}, Modifier.fillMaxWidth(), enabled = false)
            SearchCaption("추가 도서를 불러오는 중이에요. 기존 결과는 유지됩니다.")
        }
        BookSearchPageStatus.End -> SearchCaption("모든 검색 결과를 확인했어요", centered = true)
        BookSearchPageStatus.Error -> Unit
    }
}

@Composable
private fun SearchCaption(text: String, centered: Boolean = false) {
    BasicText(
        text,
        Modifier.fillMaxWidth(),
        style = BSTheme.typography.bodySmall.copy(
            color = BSTheme.colors.supportNormal,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        ),
    )
}

@Preview(name = "검색 · 기본", showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun BookSearchContentPreview() = BSTheme {
    BookSearchContent(
        query = "",
        books = emptyList(),
        status = BookSearchStatus.Initial,
        sort = BookSearchSort.Accuracy,
        pageStatus = BookSearchPageStatus.MoreAvailable,
        onQueryChange = {},
        onSearch = {},
        onSortChange = {},
        onBookClick = {},
        onFavoriteChange = { _, _ -> },
        onLoadMore = {},
        onRetry = {},
    )
}

@Preview
@Composable
private fun BookSearchTopBarPreview() = BSTheme {
    BookSearchTopBar()
}

@Preview
@Composable
private fun BookSearchPaginationPreview() = BSTheme {
    BookSearchPagination(status = BookSearchPageStatus.MoreAvailable, onLoadMore = {})
}

@Preview
@Composable
private fun SearchCaptionPreview() = BSTheme {
    SearchCaption("책을 찾고 있어요…")
}
