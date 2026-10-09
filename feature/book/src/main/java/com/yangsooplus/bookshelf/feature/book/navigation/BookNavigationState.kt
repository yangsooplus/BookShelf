package com.yangsooplus.bookshelf.feature.book.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import com.yangsooplus.bookshelf.domain.book.model.Book
import kotlinx.serialization.serializer

internal enum class BookTab { Search, Favorites }

internal class BookNavigationState(
    selectedTab: MutableState<BookTab>,
    val searchStack: NavBackStack<BookRoute>,
    val favoritesStack: NavBackStack<BookRoute>,
) {
    var selectedTab by selectedTab
        private set

    val currentStack: NavBackStack<BookRoute>
        get() = when (selectedTab) {
            BookTab.Search -> searchStack
            BookTab.Favorites -> favoritesStack
        }

    fun selectTab(tab: BookTab) {
        selectedTab = tab
    }

    fun openDetail(book: Book) {
        if (currentStack.last() !is BookRoute.Detail) {
            currentStack.add(BookRoute.Detail(book = book, source = selectedTab))
        }
    }

    fun goBack() {
        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        } else {
            selectedTab = BookTab.Search
        }
    }
}

@Composable
internal fun rememberBookNavigationState(): BookNavigationState {
    val selectedTab = rememberSaveable { mutableStateOf(BookTab.Search) }
    val searchStack = rememberSerializable(serializer = serializer<NavBackStack<BookRoute>>()) {
        NavBackStack<BookRoute>(BookRoute.Search)
    }
    val favoritesStack = rememberSerializable(serializer = serializer<NavBackStack<BookRoute>>()) {
        NavBackStack<BookRoute>(BookRoute.Favorites)
    }
    return remember { BookNavigationState(selectedTab, searchStack, favoritesStack) }
}
