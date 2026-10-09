package com.yangsooplus.bookshelf.feature.book.util

import com.yangsooplus.bookshelf.domain.book.model.Book
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val bookDateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

internal fun LocalDate.formatBookDate(): String = format(bookDateFormat)

internal fun Int.formatWonPrice(): String = "${NumberFormat.getIntegerInstance(Locale.KOREA).format(this)}원"

internal fun Book.formatAuthorPublisher(): String {
    val authorText = authors.joinToString(separator = ", ").ifBlank { "저자 미상" }
    val publisherText = publisher.ifBlank { "출판사 미상" }
    return "$authorText · $publisherText"
}

internal fun Book.formatPublishedDate(): String =
    if (publishedDate == LocalDate.MIN) "출간일 미상" else publishedDate.formatBookDate()

internal fun Book.formatPrice(): String = price.representPrice.let { price ->
    if (price < 0) "가격 정보 없음" else price.formatWonPrice()
}
