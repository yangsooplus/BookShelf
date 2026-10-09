package com.yangsooplus.bookshelf.data.datasource.network.book.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookSearchResponse(
    val meta: BookSearchMeta,
    val documents: List<BookDocument>,
)

@Serializable
data class BookSearchMeta(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("pageable_count") val pageableCount: Int,
    @SerialName("is_end") val isEnd: Boolean,
)

@Serializable
data class BookDocument(
    val title: String?,
    val contents: String?,
    val url: String?,
    val isbn: String?,
    val datetime: String?,
    val authors: List<String>?,
    val publisher: String?,
    val translators: List<String>?,
    val price: Int?,
    @SerialName("sale_price") val salePrice: Int?,
    val thumbnail: String?,
    val status: String?,
)
