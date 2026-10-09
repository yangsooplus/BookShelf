package com.yangsooplus.bookshelf.data.datasource.database.book

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
abstract class FavoriteBookDao {
    @Upsert
    abstract suspend fun upsert(book: FavoriteBookEntity)

    @Query("DELETE FROM favorite_books WHERE id = :id")
    abstract suspend fun deleteById(id: String)

    @Query("SELECT * FROM favorite_books WHERE id = :id")
    abstract suspend fun getById(id: String): FavoriteBookEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_books WHERE id = :id)")
    abstract suspend fun isFavorite(id: String): Boolean

    @Query("""
        SELECT COUNT(*) AS totalCount,
               MAX(CASE WHEN regular_price >= 0 THEN regular_price END) AS maxPrice
        FROM favorite_books
    """)
    abstract suspend fun getFavoriteMetaData(): FavoriteBookMetaData

    suspend fun getFavoriteBooks(
        query: String,
        ascending: Boolean,
        minPrice: Int?,
        maxPrice: Int?,
        limit: Int,
        offset: Int,
    ): List<FavoriteBookEntity> = if (ascending) {
        getFavoriteBooksAscending(query = query, minPrice = minPrice, maxPrice = maxPrice, limit = limit, offset = offset)
    } else {
        getFavoriteBooksDescending(query = query, minPrice = minPrice, maxPrice = maxPrice, limit = limit, offset = offset)
    }

    @Query(FAVORITE_BOOKS_QUERY + " ORDER BY title ASC, id ASC LIMIT :limit OFFSET :offset")
    protected abstract suspend fun getFavoriteBooksAscending(
        query: String,
        minPrice: Int?,
        maxPrice: Int?,
        limit: Int,
        offset: Int,
    ): List<FavoriteBookEntity>

    @Query(FAVORITE_BOOKS_QUERY + " ORDER BY title DESC, id DESC LIMIT :limit OFFSET :offset")
    protected abstract suspend fun getFavoriteBooksDescending(
        query: String,
        minPrice: Int?,
        maxPrice: Int?,
        limit: Int,
        offset: Int,
    ): List<FavoriteBookEntity>
}

private const val FAVORITE_BOOKS_QUERY = """
    SELECT * FROM favorite_books
    WHERE (:query = '' OR instr(lower(title), lower(:query)) > 0)
      AND (:minPrice IS NULL OR
        CASE WHEN sale_price >= 0 THEN sale_price
             WHEN regular_price >= 0 THEN regular_price END >= :minPrice)
      AND (:maxPrice IS NULL OR
        CASE WHEN sale_price >= 0 THEN sale_price
             WHEN regular_price >= 0 THEN regular_price END <= :maxPrice)
"""
