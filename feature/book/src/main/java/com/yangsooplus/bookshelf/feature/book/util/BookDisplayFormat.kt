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

internal fun Book.formatRegularPrice(): String =
    if (price.regularPrice < 0) "정가 정보 없음" else "정가 ${price.regularPrice.formatWonPrice()}"

internal fun Book.formatSalePriceCaption(): String? {
    val salePrice = price.salePrice?.takeIf { it >= 0 } ?: return null
    val regularPrice = price.regularPrice
    if (regularPrice <= 0 || salePrice >= regularPrice) return "판매가"
    val discountPercent = ((regularPrice.toLong() - salePrice) * 100 / regularPrice).toInt()
    return "판매가 · ${discountPercent}% 할인"
}

internal fun Book.formatInformation(): String = buildList {
    add(element = "ISBN ${isbn.ifBlank { "정보 없음" }}")
    add(element = "출판사 ${publisher.ifBlank { "정보 없음" }}")
    add(element = "저자 ${authors.joinToString(separator = ", ").ifBlank { "정보 없음" }}")
    if (translators.isNotEmpty()) add(element = "번역자 ${translators.joinToString(separator = ", ")}")
    add(element = "출간일 ${formatPublishedDate()}")
}.joinToString(separator = "\n")

internal fun Book.formatContents(): String = contents.ifBlank { "책 소개가 제공되지 않았어요." }
