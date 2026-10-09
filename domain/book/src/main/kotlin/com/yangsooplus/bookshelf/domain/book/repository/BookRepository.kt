package com.yangsooplus.bookshelf.domain.book.repository

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book

interface BookRepository {
    @Throws(BookException.NoSearchResults::class, BookException.NoMoreBooks::class)
    suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book>
    /** 제목 부분 일치로 로컬 검색하며 빈 query는 전체 조회한다. 검색·필터·정렬 후 페이지를 추출한다. */
    @Throws(BookException.NoSearchResults::class, BookException.NoMoreBooks::class)
    suspend fun getFavoriteBooks(query: String, sort: String, minPrice: Int?, maxPrice: Int?, page: Int, size: Int): List<Book>
    suspend fun setFavorite(book: Book, isFavorite: Boolean)

}
