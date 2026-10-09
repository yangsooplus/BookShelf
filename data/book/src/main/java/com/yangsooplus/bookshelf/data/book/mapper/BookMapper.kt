package com.yangsooplus.bookshelf.data.book.mapper

import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookDocument
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchResponse
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.time.LocalDate
import java.time.OffsetDateTime

internal fun BookSearchResponse.toBooks(): List<Book> = documents.map { it.toBook() }

internal fun BookDocument.toBook(): Book {
    val originalIsbn = isbn.orEmpty()
    return Book(
        id = normalizeIsbn13(originalIsbn),
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
