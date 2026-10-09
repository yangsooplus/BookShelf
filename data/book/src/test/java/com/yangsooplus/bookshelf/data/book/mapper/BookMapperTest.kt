package com.yangsooplus.bookshelf.data.book.mapper

import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookDocument
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchMeta
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchResponse
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BookMapperTest {
    @Test
    fun `검색 응답에 도서 정보가 있을 때_Book으로 변환하면_ID를 생성하고 원문 정보를 보존한다`() {
        val document = document()
        val expected = Book(
            id = "9780132350884",
            title = "도서",
            contents = "책 소개",
            url = "https://example.com/book",
            authors = listOf("저자1", "저자2"),
            publisher = "출판사",
            publishedDate = LocalDate.of(2026, 10, 9),
            price = BookPrice(regularPrice = 20000, salePrice = 18000),
            thumbnailUrl = "https://example.com/cover.jpg",
            isbn = "0132350882 9780132350884",
            translators = listOf("번역자"),
        )

        val book = document.toBook()

        assertEquals(expected, book)
    }

    @Test
    fun `ISBN은 같고 제목과 URL이 변경됐을 때_Book으로 변환하면_같은 ID를 유지한다`() {
        val original = document()
        val changed = original.copy(title = "변경 제목", url = "https://example.com/new", price = 30000)

        val originalId = original.toBook().id
        val changedId = changed.toBook().id

        assertEquals(originalId, changedId)
    }

    @Test
    fun `판매가가 미제공 또는 음수일 때_Book으로 변환하면_판매가를 null로 처리한다`() {
        val documents = listOf(document().copy(salePrice = null), document().copy(salePrice = -1))

        val prices = documents.map { it.toBook().price }

        assertEquals(List(2) { BookPrice(regularPrice = 20000, salePrice = null) }, prices)
    }

    @Test
    fun `판매가가 0원일 때_Book으로 변환하면_유효한 판매가로 유지한다`() {
        val document = document().copy(salePrice = 0)

        val price = document.toBook().price

        assertEquals(BookPrice(regularPrice = 20000, salePrice = 0), price)
    }

    @Test
    fun `출간 시간의 UTC 날짜가 다를 때_Book으로 변환하면_응답의 현지 출간일을 유지한다`() {
        val document = document().copy(datetime = "2026-10-09T00:00:00.000+09:00")

        val date = document.toBook().publishedDate

        assertEquals(LocalDate.of(2026, 10, 9), date)
    }

    @Test
    fun `ISBN 외 정보가 누락됐을 때_Book으로 변환하면_기본값을 사용한다`() {
        val document = document().copy(
            title = null, contents = null, url = null, authors = null,
            publisher = null, datetime = null, price = null, salePrice = null,
            thumbnail = null, translators = null,
        )

        val book = document.toBook()

        assertEquals(
            Book(
                id = "9780132350884",
                title = "", contents = "", url = "", authors = emptyList(), publisher = "",
                publishedDate = LocalDate.MIN,
                price = BookPrice(regularPrice = -1, salePrice = null),
                thumbnailUrl = "",
                isbn = "0132350882 9780132350884",
                translators = emptyList(),
            ),
            book,
        )
    }

    @Test
    fun `ISBN이 누락됐을 때_Book으로 변환하면_오류를 전달한다`() {
        val document = document().copy(isbn = null)

        assertThrows(IllegalArgumentException::class.java) { document.toBook() }
    }

    @Test
    fun `출간일 형식이 잘못됐을 때_Book으로 변환하면_오류를 전달한다`() {
        val document = document().copy(datetime = "출간일 미상")

        assertThrows(DateTimeParseException::class.java) { document.toBook() }
    }

    @Test
    fun `표시 정보가 빈 값일 때_Book으로 변환하면_빈 값을 유지한다`() {
        val document = document().copy(contents = "", authors = emptyList(), publisher = "", thumbnail = "", translators = emptyList())

        val book = document.toBook()

        assertEquals("", book.contents)
        assertEquals(emptyList<String>(), book.authors)
        assertEquals("", book.publisher)
        assertEquals("", book.thumbnailUrl)
        assertEquals(emptyList<String>(), book.translators)
    }

    @Test
    fun `검색 결과에 여러 도서가 있을 때_목록으로 변환하면_순서를 유지하고 각각 ID를 생성한다`() {
        val response = BookSearchResponse(
            meta = BookSearchMeta(totalCount = 2, pageableCount = 2, isEnd = true),
            documents = listOf(document().copy(isbn = "0132350882"), document().copy(isbn = "080442957X")),
        )

        val books = response.toBooks()

        assertEquals(listOf("9780132350884", "9780804429573"), books.map { it.id })
    }

    @Test
    fun `검색 결과가 비어 있을 때_목록으로 변환하면_빈 목록을 반환한다`() {
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 0, pageableCount = 0, isEnd = true), documents = emptyList())

        val books = response.toBooks()

        assertEquals(emptyList<Book>(), books)
    }

    @Test
    fun `검색 결과에 잘못된 ISBN이 있을 때_목록으로 변환하면_도서를 조용히 제외하지 않고 오류를 전달한다`() {
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 2, pageableCount = 2, isEnd = true), documents = listOf(document(), document().copy(isbn = "")))

        assertThrows(IllegalArgumentException::class.java) { response.toBooks() }
    }

    private fun document() = BookDocument(
        title = "도서",
        contents = "책 소개",
        url = "https://example.com/book",
        isbn = "0132350882 9780132350884",
        datetime = "2026-10-09T12:30:00.000+09:00",
        authors = listOf("저자1", "저자2"),
        publisher = "출판사",
        translators = listOf("번역자"),
        price = 20000,
        salePrice = 18000,
        thumbnail = "https://example.com/cover.jpg",
        status = "정상판매",
    )
}
