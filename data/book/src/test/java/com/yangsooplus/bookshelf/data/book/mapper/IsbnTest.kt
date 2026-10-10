package com.yangsooplus.bookshelf.data.book.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class IsbnTest {
    @Test
    fun `같은 도서의 ISBN 표기가 다를 때_정규화하면_같은 ID를 반환한다`() {
        val inputs = listOf("0132350882", "9780132350884", "0132350882 9780132350884")

        val ids = inputs.map(::normalizeIsbn13)

        assertEquals(List(3) { "9780132350884" }, ids)
    }

    @Test
    fun `두 ISBN의 순서와 공백이 다를 때_정규화하면_같은 ID를 반환한다`() {
        val isbn = " \t9780132350884   0132350882\n"

        val id = normalizeIsbn13(isbn = isbn)

        assertEquals("9780132350884", id)
    }

    @Test
    fun `ISBN10의 체크 문자가 X일 때_정규화하면_ISBN13으로 변환한다`() {
        val isbn = "080442957X"

        val id = normalizeIsbn13(isbn = isbn)

        assertEquals("9780804429573", id)
    }

    @Test
    fun `ISBN13이 979로 시작할 때_정규화하면_그대로 유지한다`() {
        val isbn = "9791090636071"

        val id = normalizeIsbn13(isbn = isbn)

        assertEquals(isbn, id)
    }

    @Test
    fun `ISBN10과 979 ISBN13이 함께 제공될 때_정규화하면_응답의 ISBN13을 사용한다`() {
        val isbn = "1141114712 9791141114718"

        val id = normalizeIsbn13(isbn = isbn)

        assertEquals("9791141114718", id)
    }

    @Test
    fun `두 ISBN의 변환 결과가 다를 때_정규화하면_제공된 ISBN13을 우선한다`() {
        val isbn = "0132350882 9780306406157"

        val id = normalizeIsbn13(isbn = isbn)

        assertEquals("9780306406157", id)
    }

    @Test
    fun `ISBN의 체크 숫자가 잘못됐을 때_정규화하면_오류를 전달한다`() {
        val inputs = listOf("0132350883", "9780132350885", "0804429570")

        inputs.forEach { isbn ->
            assertThrows(isbn, IllegalArgumentException::class.java) { normalizeIsbn13(isbn = isbn) }
        }
    }

    @Test
    fun `ISBN의 형식이 잘못됐을 때_정규화하면_오류를 전달한다`() {
        val inputs = listOf("", " ", "123", "01323508X2", "978013235088X", "4006381333931", "９７８０１３２３５０８８４")

        inputs.forEach { isbn ->
            assertThrows(isbn, IllegalArgumentException::class.java) { normalizeIsbn13(isbn = isbn) }
        }
    }
}
