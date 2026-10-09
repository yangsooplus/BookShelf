package com.yangsooplus.bookshelf.feature.book.screen.favorites.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.component.BSSearchBar
import com.yangsooplus.bookshelf.core.designsystem.component.BSStateMessage
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBanner
import com.yangsooplus.bookshelf.core.designsystem.component.BSStatusBannerType
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.feature.book.component.BookCard
import com.yangsooplus.bookshelf.feature.book.component.BookCardSkeleton
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksPageStatus
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksStatus

@Composable
internal fun FavoriteBooksContent(
    query: String,
    books: List<Book>,
    status: FavoriteBooksStatus,
    sort: FavoriteBooksSort,
    pageStatus: FavoriteBooksPageStatus,
    totalCount: Int,
    minPriceFilter: Int?,
    maxPriceFilter: Int?,
    priceRangeUpperBound: Int,
    isPriceFilterEnabled: Boolean,
    isSortPanelVisible: Boolean,
    isPricePanelVisible: Boolean,
    onOpenPricePanel: () -> Unit,
    onClosePricePanel: () -> Unit,
    onApplyPriceFilter: (Int, Int) -> Unit,
    onResetPriceFilter: () -> Unit,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSortChange: (FavoriteBooksSort) -> Unit,
    onOpenSortPanel: () -> Unit,
    onCloseSortPanel: () -> Unit,
    onBookClick: (Book) -> Unit,
    onFavoriteChange: (Book, Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    columns: Int? = null,
) {
    val gridState = rememberLazyGridState()
    val currentOnLoadMore by rememberUpdatedState(newValue = onLoadMore)

    LaunchedEffect(key1 = status) {
        if (status == FavoriteBooksStatus.Loading) gridState.scrollToItem(index = 0)
    }
    LaunchedEffect(key1 = status, key2 = pageStatus, key3 = books.size) {
        if (status != FavoriteBooksStatus.Results || pageStatus != FavoriteBooksPageStatus.MoreAvailable) {
            return@LaunchedEffect
        }
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val isNearEnd = lastVisibleIndex >= layoutInfo.totalItemsCount - 4
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= 0 && isNearEnd
        }.collect { isNearEnd ->
            if (isNearEnd) currentOnLoadMore()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = BSTheme.colors.backgroundSecondary)
    ) {
        FavoriteBooksTopBar()

        LazyVerticalGrid(
            state = gridState,
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
                    placeholder = "저장한 책 제목을 검색하세요",
                )
            }

            item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                FavoriteBooksControls(
                    totalCount = totalCount,
                    sort = sort,
                    isPriceFilterApplied = minPriceFilter != null || maxPriceFilter != null,
                    onOpenPricePanel = onOpenPricePanel,
                    onOpenSortPanel = onOpenSortPanel,
                )
            }
            when (status) {
                FavoriteBooksStatus.Loading -> {
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) { SearchCaption(text = "저장한 책을 불러오고 있어요…") }
                    items(count = 3) { BookCardSkeleton() }
                }

                FavoriteBooksStatus.Empty -> item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    if (totalCount == 0) {
                        BSStateMessage(title = "아직 저장한 책이 없어요", description = "마음에 드는 책의 하트를 눌러 저장해보세요.", icon = BSIcon.Heart) {
                            BSButton(label = "책 검색하기", onClick = onSearchClick, modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        BSStateMessage(title = "조건에 맞는 책이 없어요", description = "검색어나 가격 조건을 변경해보세요.") {
                            if (minPriceFilter != null || maxPriceFilter != null) {
                                BSButton(label = "가격 필터 초기화", onClick = onResetPriceFilter, modifier = Modifier.fillMaxWidth())
                            } else {
                                BSButton(
                                    label = "전체 즐겨찾기 보기",
                                    onClick = {
                                        onQueryChange("")
                                        onSearch("")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }

                FavoriteBooksStatus.Error -> item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    BSStateMessage(
                        title = "저장한 책을 불러오지 못했어요",
                        description = "잠시 후 다시 시도해주세요.",
                        icon = BSIcon.Offline
                    ) {
                        BSButton(
                            label = "다시 시도",
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                FavoriteBooksStatus.Results -> {
                    if (pageStatus == FavoriteBooksPageStatus.Error) {
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
                        SearchCaption(text = "저장한 도서는 인터넷 없이도 볼 수 있어요")
                    }
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        FavoriteBooksPagination(
                            status = pageStatus,
                        )
                    }
                }
            }
        }
    }
    if (isSortPanelVisible) {
        FavoriteBooksSortPanel(
            sort = sort,
            onDismiss = onCloseSortPanel,
            onApply = onSortChange,
        )
    }
    if (isPricePanelVisible) {
        FavoriteBooksPricePanel(
            minPrice = minPriceFilter,
            maxPrice = maxPriceFilter,
            upperBound = priceRangeUpperBound,
            enabled = isPriceFilterEnabled,
            onDismiss = onClosePricePanel,
            onApply = onApplyPriceFilter,
            onReset = onResetPriceFilter,
        )
    }
}

@Composable
private fun FavoriteBooksTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .background(color = BSTheme.colors.backgroundPrimary)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = "즐겨찾기",
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
private fun FavoriteBooksPagination(status: FavoriteBooksPageStatus) {
    when (status) {
        FavoriteBooksPageStatus.Loading -> SearchCaption(text = "추가 도서를 불러오는 중이에요…", centered = true)
        FavoriteBooksPageStatus.End -> SearchCaption(text = "모든 즐겨찾기를 확인했어요", centered = true)
        FavoriteBooksPageStatus.MoreAvailable, FavoriteBooksPageStatus.Error -> Unit
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

@Preview(name = "즐겨찾기 · 기본", showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun FavoriteBooksContentPreview() = BSTheme {
    FavoriteBooksContent(
        query = "",
        books = emptyList(),
        status = FavoriteBooksStatus.Empty,
        sort = FavoriteBooksSort.TitleAscending,
        pageStatus = FavoriteBooksPageStatus.End,
        totalCount = 0,
        minPriceFilter = null,
        maxPriceFilter = null,
        priceRangeUpperBound = 10_000,
        isPriceFilterEnabled = false,
        isSortPanelVisible = false,
        isPricePanelVisible = false,
        onOpenPricePanel = {},
        onClosePricePanel = {},
        onApplyPriceFilter = { _, _ -> },
        onResetPriceFilter = {},
        onSearchClick = {},
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
private fun FavoriteBooksTopBarPreview() = BSTheme {
    FavoriteBooksTopBar()
}

@Preview
@Composable
private fun FavoriteBooksPaginationPreview() = BSTheme {
    FavoriteBooksPagination(status = FavoriteBooksPageStatus.Loading)
}

@Preview
@Composable
private fun SearchCaptionPreview() = BSTheme {
    SearchCaption(text = "저장한 책을 불러오고 있어요…")
}
