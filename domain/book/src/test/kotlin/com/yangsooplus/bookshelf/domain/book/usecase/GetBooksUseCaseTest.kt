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
        coEvery { bookRepository.getBooks(query = "기록", sort = "latest", page = 2, size = 20) } returns books

        val result = SUT.invoke(args = GetBooksUseCase.Param(query = "  기록  ", sort = GetBooksUseCase.Param.Sort.LATEST, page = 2))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.Success(books = books))
    }

    @Test
    fun `검색 결과가 없을 때_도서를 조회하면_NoSearchResults를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(query = any(), sort = any(), page = any(), size = any()) } throws BookException.NoSearchResults()

        val result = SUT.invoke(args = param())

        assertThat(result).isEqualTo(GetBooksUseCase.Result.NoSearchResults)
    }

    @Test
    fun `더 가져올 도서가 없을 때_다음 페이지를 조회하면_NoMoreBooks를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(query = any(), sort = any(), page = any(), size = any()) } throws BookException.NoMoreBooks()

        val result = SUT.invoke(args = param(page = 2))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.NoMoreBooks)
    }

    @Test
    fun `도서 조회가 실패할 때_도서를 조회하면_Fail을 반환한다`() = runTest {
        val failure = IllegalStateException("Search failed")
        coEvery { bookRepository.getBooks(query = any(), sort = any(), page = any(), size = any()) } throws failure

        val result = SUT.invoke(args = param())

        assertThat(result).isEqualTo(GetBooksUseCase.Result.Fail(cause = failure))
    }

    @Test
    fun `도서 조회 중 취소되면_취소를 전파한다`() = runTest {
        val cancellation = CancellationException("Cancelled")
        coEvery { bookRepository.getBooks(query = any(), sort = any(), page = any(), size = any()) } throws cancellation

        val result = assertThrows<CancellationException> { SUT.invoke(args = param()) }

        assertThat(result).isSameInstanceAs(cancellation)
    }

    @Test
    fun `검색어가 공백일 때_도서를 조회하면_InvalidQuery를 반환하고 조회하지 않는다`() = runTest {
        val query = "  "

        val result = SUT.invoke(args = param(query = query))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.InvalidQuery)
        coVerify(exactly = 0) { bookRepository.getBooks(query = any(), sort = any(), page = any(), size = any()) }
    }

    @Test
    fun `Repository가 페이지 오류를 전달할 때_도서를 조회하면_InvalidPage를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 0, size = 20) } throws
            BookException.InvalidPage(message = "Page must be positive")

        val result = SUT.invoke(args = param(page = 0))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.InvalidPage)
        coVerify(exactly = 1) { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 0, size = 20) }
    }

    @Test
    fun `페이지가 전달됐을 때_도서를 조회하면_페이지 검증을 Repository에 맡긴다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 51, size = 20) } returns books

        val result = SUT.invoke(args = param(page = 51))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.Success(books = books))
        coVerify(exactly = 1) { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 51, size = 20) }
    }

    @Test
    fun `Repository가 페이지 상한 초과를 전달할 때_도서를 조회하면_InvalidPage를 반환한다`() = runTest {
        coEvery { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 51, size = 20) } throws
            BookException.InvalidPage(message = "Page exceeds provider limit")

        val result = SUT.invoke(args = param(page = 51))

        assertThat(result).isEqualTo(GetBooksUseCase.Result.InvalidPage)
        coVerify(exactly = 1) { bookRepository.getBooks(query = "기록", sort = "accuracy", page = 51, size = 20) }
    }

    private fun param(query: String = "기록", page: Int = 1) =
        GetBooksUseCase.Param(query = query, sort = GetBooksUseCase.Param.Sort.ACCURACY, page = page)
}
