package com.yangsooplus.bookshelf.feature.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yangsooplus.bookshelf.core.designsystem.component.BSBottomNavigation
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.component.BSNavigationItem
import com.yangsooplus.bookshelf.core.designsystem.component.BSNavigationRail
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.navigation.BookNavigationState
import com.yangsooplus.bookshelf.feature.book.navigation.BookRoute
import com.yangsooplus.bookshelf.feature.book.navigation.BookTab
import com.yangsooplus.bookshelf.feature.book.navigation.rememberBookNavigationState
import com.yangsooplus.bookshelf.feature.book.screen.BookDetailScreen
import com.yangsooplus.bookshelf.feature.book.screen.BookSearchScreen
import com.yangsooplus.bookshelf.feature.book.screen.FavoriteBooksScreen

private val navigationItems = listOf(
    BSNavigationItem(label = "검색", icon = BSIcon.Search),
    BSNavigationItem(label = "즐겨찾기", icon = BSIcon.Heart),
)

@Composable
fun BookEntry(modifier: Modifier = Modifier) {
    BookNavigationHost(navigation = rememberBookNavigationState(), modifier = modifier)
}

@Composable
internal fun BookNavigationHost(navigation: BookNavigationState, modifier: Modifier = Modifier) {
    val provider = entryProvider {
        entry<BookRoute.Search> { BookSearchScreen(onBookClick = navigation::openDetail) }
        entry<BookRoute.Favorites> { FavoriteBooksScreen(onBookClick = navigation::openDetail) }
        entry<BookRoute.Detail> { route ->
            BookDetailScreen(book = route.book, onBack = navigation::goBack)
        }
    }
    val searchEntries = rememberDecoratedNavEntries(
        backStack = navigation.searchStack,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = provider,
    )
    val favoriteEntries = rememberDecoratedNavEntries(
        backStack = navigation.favoritesStack,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = provider,
    )
    val entries = when (navigation.selectedTab) {
        BookTab.Search -> searchEntries
        BookTab.Favorites -> searchEntries + favoriteEntries
    }
    val selectTab: (Int) -> Unit = { navigation.selectTab(BookTab.entries[it]) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
            .background(BSTheme.colors.backgroundSecondary)
            .safeDrawingPadding(),
    ) {
        if (maxWidth >= 600.dp && maxHeight >= 480.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                BSNavigationRail(navigationItems, navigation.selectedTab.ordinal, selectTab)
                NavDisplay(entries = entries, onBack = navigation::goBack, modifier = Modifier.weight(1f))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                NavDisplay(entries = entries, onBack = navigation::goBack, modifier = Modifier.weight(1f))
                BSBottomNavigation(navigationItems, navigation.selectedTab.ordinal, selectTab)
            }
        }
    }
}