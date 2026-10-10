package com.yangsooplus.bookshelf.feature.book.navigation

import androidx.navigation3.runtime.NavKey
import com.yangsooplus.bookshelf.domain.book.model.Book
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface BookRoute : NavKey {
    @Serializable
    data object Search : BookRoute

    @Serializable
    data object Favorites : BookRoute

    @Serializable
    data class Detail(
        @Serializable(with = BookSerializer::class) val book: Book,
        val source: BookTab,
    ) : BookRoute
}
