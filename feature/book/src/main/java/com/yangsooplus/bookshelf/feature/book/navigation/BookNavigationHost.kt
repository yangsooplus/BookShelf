package com.yangsooplus.bookshelf.feature.book.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yangsooplus.bookshelf.feature.book.layout.BookAdaptiveLayout
import com.yangsooplus.bookshelf.feature.book.layout.currentBookLayoutInfo
import com.yangsooplus.bookshelf.feature.book.screen.BookDetailScreen
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchScreen
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BookNavigationHost(navigation: BookNavigationState, modifier: Modifier = Modifier) {
    val layoutInfo = currentBookLayoutInfo()
    val entries = rememberBookNavEntries(navigation)
    val listDetailStrategy = rememberListDetailSceneStrategy<BookRoute>(
        directive = layoutInfo.paneDirective,
    )

    BookAdaptiveLayout(
        useRail = layoutInfo.useRail,
        selectedTab = navigation.selectedTab,
        onSelectTab = navigation::selectTab,
        modifier = modifier,
    ) { contentModifier ->
        NavDisplay(
            entries = entries,
            sceneStrategies = listOf(listDetailStrategy),
            onBack = navigation::goBack,
            modifier = contentModifier,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun rememberBookNavEntries(navigation: BookNavigationState): List<NavEntry<BookRoute>> {
    val provider = entryProvider<BookRoute> {
        entry<BookRoute.Search>(metadata = ListDetailSceneStrategy.listPane()) {
            BookSearchScreen(onBookClick = navigation::openDetail)
        }
        entry<BookRoute.Favorites>(metadata = ListDetailSceneStrategy.listPane()) {
            FavoriteBooksScreen(
                onBookClick = navigation::openDetail,
                onSearchClick = { navigation.selectTab(tab = BookTab.Search) },
            )
        }
        entry<BookRoute.Detail>(metadata = ListDetailSceneStrategy.detailPane()) { route ->
            BookDetailScreen(book = route.book, onBack = navigation::goBack)
        }
    }
    val searchEntries = rememberDecoratedNavEntries(
        backStack = navigation.searchStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = provider,
    )
    val favoriteEntries = rememberDecoratedNavEntries(
        backStack = navigation.favoritesStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = provider,
    )
    return when (navigation.selectedTab) {
        BookTab.Search -> searchEntries
        BookTab.Favorites -> favoriteEntries
    }
}
