package com.yangsooplus.bookshelf.feature.book.screen.search.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSButtonStyle
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.component.BSSearchBar
import com.yangsooplus.bookshelf.core.designsystem.component.BSStateMessage
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBanner
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBannerType
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.component.BookCard
import com.yangsooplus.bookshelf.feature.book.component.BookCardSkeleton
import com.yangsooplus.bookshelf.domain.book.model.Book

@Composable
internal fun BookSearchContent(
    query: String,
    books: List<Book>,
    status: BookSearchStatus,
    sort: BookSearchSort,
    pageStatus: BookSearchPageStatus,
    isSortPanelVisible: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSortChange: (BookSearchSort) -> Unit,
    onOpenSortPanel: () -> Unit,
    onCloseSortPanel: () -> Unit,
    onBookClick: (Book) -> Unit,
    onFavoriteChange: (Book, Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    columns: Int? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = BSTheme.colors.backgroundSecondary)
    ) {
        BookSearchTopBar()

        LazyVerticalGrid(
            columns = columns?.let { GridCells.Fixed(count = it) } ?: GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(all = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                BSSearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                )
            }

            if (status == BookSearchStatus.Results || status == BookSearchStatus.Empty) {
                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.weight(weight = 1f))
                        BSButton(
                            label = sort.label,
                            onClick = onOpenSortPanel,
                            modifier = Modifier.width(width = 106.dp),
                            style = BSButtonStyle.Subtle
                        )
                    }
                }
            }
            when (status) {
                BookSearchStatus.Initial -> item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    BSStateMessage(title = "읽고 싶은 책을 찾아보세요", description = "제목이나 저자로 검색할 수 있어요.")
                }

                BookSearchStatus.Loading -> {
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) { SearchCaption(text = "책을 찾고 있어요…") }
                    items(count = 3) { BookCardSkeleton() }
                }

                BookSearchStatus.Empty -> item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    BSStateMessage(title = "검색 결과가 없어요", description = "다른 제목이나 저자로 다시 검색해보세요.") {
                        BSButton(
                            label = "검색어 지우기",
                            onClick = { onQueryChange("") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                BookSearchStatus.Error -> item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    BSStateMessage(
                        title = "검색 결과를 불러오지 못했어요",
                        description = "네트워크 연결을 확인한 뒤 다시 시도해주세요.",
                        icon = BSIcon.Offline
                    ) {
                        BSButton(
                            label = "다시 시도",
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                BookSearchStatus.Results -> {
                    if (pageStatus == BookSearchPageStatus.Error) {
                        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                                BSStatusBanner(message = "다음 페이지를 불러오지 못했어요", type = BSStatusBannerType.Retry)
                                BSButton(
                                    label = "다음 페이지 다시 시도",
                                    onClick = onLoadMore,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                SearchCaption(text = "기존 결과는 유지됩니다.")
                            }
                        }
                    }
                    items(items = books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            onFavoriteChange = { onFavoriteChange(book, it) },
                        )
                    }
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        BookSearchPagination(
                            status = pageStatus,
                            onLoadMore = onLoadMore,
                        )
                    }
                }
            }
        }
    }
    if (isSortPanelVisible) {
        BookSearchSortPanel(
            sort = sort,
            onDismiss = onCloseSortPanel,
            onApply = onSortChange,
        )
    }
}

@Composable
private fun BookSearchTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .background(color = BSTheme.colors.backgroundPrimary)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = "도서 검색",
            modifier = Modifier.weight(weight = 1f),
            style = BSTheme.typography.displaySmall.copy(color = BSTheme.colors.contentPrimary),
        )
        Box(modifier = Modifier.width(width = 24.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 24.dp)
                    .background(color = BSTheme.colors.buttonPrimary, shape = RoundedCornerShape(size = 4.dp))
            )
        }
    }
}

@Composable
private fun BookSearchPagination(status: BookSearchPageStatus, onLoadMore: () -> Unit) {
    when (status) {
        BookSearchPageStatus.MoreAvailable -> BSButton(
            label = "20개 더 보기",
            onClick = onLoadMore,
            modifier = Modifier.fillMaxWidth()
        )

        BookSearchPageStatus.Loading -> Column(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
            BSButton(
                label = "추가 20개 불러오는 중…",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
            )
            SearchCaption(text = "추가 도서를 불러오는 중이에요. 기존 결과는 유지됩니다.")
        }

        BookSearchPageStatus.End -> SearchCaption(text = "모든 검색 결과를 확인했어요", centered = true)
        BookSearchPageStatus.Error -> Unit
    }
}

@Composable
private fun SearchCaption(text: String, centered: Boolean = false) {
    BasicText(
        text = text,
        modifier = Modifier.fillMaxWidth(),
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
        isSortPanelVisible = false,
        onQueryChange = {},
        onSearch = {},
        onSortChange = {},
        onOpenSortPanel = {},
        onCloseSortPanel = {},
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
    SearchCaption(text = "책을 찾고 있어요…")
}
