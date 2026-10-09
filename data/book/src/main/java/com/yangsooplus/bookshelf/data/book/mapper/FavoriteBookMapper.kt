package com.yangsooplus.bookshelf.data.book.mapper

import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookEntity
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice

internal fun Book.toFavoriteBookEntity(): FavoriteBookEntity = FavoriteBookEntity(
    id = id,
    title = title,
    contents = contents,
    url = url,
    authors = authors.toList(),
    publisher = publisher,
    publishedDate = publishedDate,
    regularPrice = price.regularPrice,
    salePrice = price.salePrice,
    thumbnailUrl = thumbnailUrl,
    isbn = isbn,
    translators = translators.toList(),
)

internal fun FavoriteBookEntity.toBook(): Book = Book(
    id = id,
    title = title,
    contents = contents,
    url = url,
    authors = authors.toList(),
    publisher = publisher,
    publishedDate = publishedDate,
    price = BookPrice(regularPrice = regularPrice, salePrice = salePrice),
    thumbnailUrl = thumbnailUrl,
    isbn = isbn,
    translators = translators.toList(),
    isFavorite = true,
)
