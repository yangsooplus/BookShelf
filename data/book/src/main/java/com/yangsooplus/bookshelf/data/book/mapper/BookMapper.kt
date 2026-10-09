package com.yangsooplus.bookshelf.data.book.mapper

import android.util.Log
import com.yangsooplus.bookshelf.data.book.BuildConfig
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookDocument
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchResponse
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.time.LocalDate
import java.time.OffsetDateTime

internal fun BookSearchResponse.toBooks(): List<Book> = documents.mapNotNull { it.toBookOrNull() }

private fun BookDocument.toBookOrNull(): Book? = try {
    toBook()
} catch (failure: IllegalArgumentException) {
    logMappingFailure(reason = if (isbn.isNullOrBlank()) "missing_isbn" else "invalid_isbn", failure = failure)
    null
}

private fun BookDocument.logMappingFailure(reason: String, failure: Exception) {
    // 추후 Crashlytics 등에 제목·원본 ISBN·실패 원인을 기록해 검색에서 제외된 도서의 CS를 추적한다.
    if (BuildConfig.DEBUG) {
        Log.d(
            "BookMapper",
            "Excluded book: reason=$reason, title=$title, isbn=$isbn, datetime=$datetime, error=${failure.message}",
        )
    }
}

internal fun BookDocument.toBook(): Book {
    val originalIsbn = isbn.orEmpty()
    return Book(
        id = normalizeIsbn13(isbn = originalIsbn),
        title = title.orEmpty(),
        contents = contents.orEmpty(),
        url = url.orEmpty(),
        authors = authors.orEmpty().toList(),
        publisher = publisher.orEmpty(),
        publishedDate = datetime?.let { OffsetDateTime.parse(it).toLocalDate() } ?: LocalDate.MIN,
        price = BookPrice(
            regularPrice = price ?: -1,
            salePrice = salePrice?.takeIf { it >= 0 },
        ),
        thumbnailUrl = thumbnail.orEmpty(),
        isbn = originalIsbn,
        translators = translators.orEmpty().toList(),
    )
}
