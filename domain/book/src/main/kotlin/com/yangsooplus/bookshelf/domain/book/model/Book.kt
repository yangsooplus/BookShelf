package com.yangsooplus.bookshelf.domain.book.model

import java.time.LocalDate

data class Book(
    val id: String,
    val title: String,
    val contents: String,
    val url: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: LocalDate,
    val price: BookPrice,
    val thumbnailUrl: String,
    val isbn: String,
    val translators: List<String>,
)

/**
 * 도서 가격 정보.
 *
 * @property regularPrice 정가. 단위는 원(KRW).
 * @property salePrice 판매가. 단위는 원(KRW)이며, 미제공 시 null.
 */
data class BookPrice(
    val regularPrice: Int,
    val salePrice: Int? = null,
) {
    val representPrice: Int get() = salePrice ?: regularPrice
}
