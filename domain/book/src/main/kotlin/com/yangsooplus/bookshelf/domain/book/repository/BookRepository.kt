package com.yangsooplus.bookshelf.domain.book.repository

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book

interface BookRepository {
    @Throws(BookException.NoSearchResults::class, BookException.NoMoreBooks::class)
    suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book>
    @Throws(BookException.NoSearchResults::class, BookException.NoMoreBooks::class)
    suspend fun getFavoriteBooks(query: String, sort: String, minPrice: Int?, maxPrice: Int?, page: Int, size: Int): List<Book>
    suspend fun setFavorite(book: Book, isFavorite: Boolean)

}
