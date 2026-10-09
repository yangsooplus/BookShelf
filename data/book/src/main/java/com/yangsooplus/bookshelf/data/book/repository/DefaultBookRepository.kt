package com.yangsooplus.bookshelf.data.book.repository

import com.yangsooplus.bookshelf.data.book.mapper.toBook
import com.yangsooplus.bookshelf.data.book.mapper.toBooks
import com.yangsooplus.bookshelf.data.book.mapper.toFavoriteBookEntity
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookDao
import com.yangsooplus.bookshelf.data.datasource.network.book.BookApiService
import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject

internal class DefaultBookRepository @Inject constructor(
    private val bookApiService: BookApiService,
    private val favoriteBookDao: FavoriteBookDao,
) : BookRepository {
    override suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book> {
        if (page !in 1..KAKAO_MAX_PAGE) {
            throw BookException.InvalidPage(message = "Kakao book search page must be between 1 and $KAKAO_MAX_PAGE")
        }
        if (size !in 1..KAKAO_MAX_PAGE_SIZE) {
            throw BookException.InvalidArgument(message = "Kakao book search size must be between 1 and $KAKAO_MAX_PAGE_SIZE")
        }
        val response = bookApiService.searchBooks(
            query = query,
            sort = sort,
            page = page,
            size = size,
        )
        if (response.documents.isEmpty()) {
            if (response.meta.totalCount == 0) throw BookException.NoSearchResults()
            throw BookException.NoMoreBooks()
        }
        return response.toBooks()
    }

    override suspend fun getFavoriteBooks(
        query: String,
        sort: String,
        minPrice: Int?,
        maxPrice: Int?,
        page: Int,
        size: Int,
    ): List<Book> {
        if (page < 1) throw BookException.InvalidPage(message = "Favorite book page must be positive")
        val ascending = when (sort) {
            "asc" -> true
            "desc" -> false
            else -> throw BookException.InvalidArgument(message = "Invalid favorite book sort: $sort")
        }
        if (size < 1) throw BookException.InvalidArgument(message = "Size must be positive")
        if (minPrice != null && minPrice < 0 || maxPrice != null && maxPrice < 0) {
            throw BookException.InvalidArgument(message = "Price bounds must be non-negative")
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw BookException.InvalidArgument(message = "Minimum price exceeds maximum price")
        }
        val offset = (page.toLong() - 1) * size
        if (offset > Int.MAX_VALUE) throw BookException.InvalidPage(message = "Favorite book offset is too large")

        val books = favoriteBookDao.getFavoriteBooks(
            query = query,
            ascending = ascending,
            minPrice = minPrice,
            maxPrice = maxPrice,
            limit = size,
            offset = offset.toInt(),
        )
        if (books.isEmpty()) {
            if (page == 1) throw BookException.NoSearchResults()
            throw BookException.NoMoreBooks()
        }
        return books.map { it.toBook() }
    }

    override suspend fun setFavorite(book: Book, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteBookDao.upsert(book = book.toFavoriteBookEntity())
        } else {
            favoriteBookDao.deleteById(id = book.id)
        }
    }

    private companion object {
        const val KAKAO_MAX_PAGE = 50
        const val KAKAO_MAX_PAGE_SIZE = 50
    }
}
