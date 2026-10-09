package com.yangsooplus.bookshelf.domain.book.usecase

import com.appmattus.kotlinfixture.kotlinFixture
import com.google.common.truth.Truth.assertThat
import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetBooksUseCaseTest {
    private lateinit var SUT: GetBooksUseCase

    @MockK
    private lateinit var bookRepository: BookRepository

    private val fixture = kotlinFixture()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        SUT = GetBooksUseCase(bookRepository = bookRepository)
    }

    @Test
    fun `검색어에 앞뒤 공백이 있을 때_발간일순으로 2페이지를 조회하면_공백을 제거한 조건으로 조회한 도서를 반환한다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getBooks("기록", "latest", 2, 20) } returns books

        val result = SUT.invoke(GetBooksUseCase.Param("  기록  ", GetBooksUseCase.Param.Sort.LATEST, 2))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.Success(books))
    }

    @Test
    fun `검색 결과가 없을 때_도서를 조회하면_NoSearchResults를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(any(), any(), any(), any()) } throws BookException.NoSearchResults()

        val result = SUT.invoke(param())

        assertThat(result).isEqualTo(GetBooksUseCase.Result.NoSearchResults)
    }

    @Test
    fun `더 가져올 도서가 없을 때_다음 페이지를 조회하면_NoMoreBooks를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(any(), any(), any(), any()) } throws BookException.NoMoreBooks()

        val result = SUT.invoke(param(page = 2))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.NoMoreBooks)
    }

    @Test
    fun `도서 조회가 실패할 때_도서를 조회하면_Fail을 반환한다`() = runTest {
        val failure = IllegalStateException("Search failed")
        coEvery { bookRepository.getBooks(any(), any(), any(), any()) } throws failure

        val result = SUT.invoke(param())

        assertThat(result).isEqualTo(GetBooksUseCase.Result.Fail(failure))
    }

    @Test
    fun `도서 조회 중 취소되면_취소를 전파한다`() = runTest {
        val cancellation = CancellationException("Cancelled")
        coEvery { bookRepository.getBooks(any(), any(), any(), any()) } throws cancellation

        val result = assertThrows<CancellationException> { SUT.invoke(param()) }

        assertThat(result).isSameInstanceAs(cancellation)
    }

    @Test
    fun `검색어가 공백이거나 페이지가 1보다 작을 때_도서를 조회하면_입력 오류를 반환하고 조회하지 않는다`() = runTest {
        val invalidQueryResult = SUT.invoke(param(query = "  "))
        val invalidPageResult = SUT.invoke(param(page = 0))

        assertThat(invalidQueryResult).isEqualTo(GetBooksUseCase.Result.InvalidQuery)
        assertThat(invalidPageResult).isEqualTo(GetBooksUseCase.Result.InvalidPage)
        coVerify(exactly = 0) { bookRepository.getBooks(any(), any(), any(), any()) }
    }

    private fun param(query: String = "기록", page: Int = 1) =
        GetBooksUseCase.Param(query, GetBooksUseCase.Param.Sort.ACCURACY, page)
}
