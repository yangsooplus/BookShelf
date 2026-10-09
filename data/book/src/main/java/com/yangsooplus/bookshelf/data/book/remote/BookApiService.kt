package com.yangsooplus.bookshelf.data.book.remote

import com.yangsooplus.bookshelf.data.book.remote.model.BookSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface BookApiService {
    @GET("v3/search/book")
    suspend fun searchBooks(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): BookSearchResponse
}
