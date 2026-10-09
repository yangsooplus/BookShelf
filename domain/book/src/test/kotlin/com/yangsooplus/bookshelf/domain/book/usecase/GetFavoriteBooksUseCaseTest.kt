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

class GetFavoriteBooksUseCaseTest {
    private lateinit var SUT: GetFavoriteBooksUseCase

    @MockK
    private lateinit var bookRepository: BookRepository

    private val fixture = kotlinFixture()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        SUT = GetFavoriteBooksUseCase(bookRepository = bookRepository)
    }

    @Test
    fun `가격 제한 없이_제목 오름차순으로 즐겨찾기를 조회하면_저장된 도서를 반환한다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getFavoriteBooks("", "asc", null, null, 1, 100) } returns books

        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.Success(books))
        coVerify(exactly = 0) { bookRepository.getBooks(any(), any(), any(), any()) }
    }

    @Test
    fun `가격 범위를 지정했을 때_제목 내림차순으로 2페이지를 조회하면_해당 조건으로 조회한 도서를 반환한다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getFavoriteBooks("", "desc", 10_000, 20_000, 2, 100) } returns books

        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.DESC, 10_000, 20_000, page = 2))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.Success(books))
    }

    @Test
    fun `최소 가격만 지정했을 때_즐겨찾기를 조회하면_최대 가격을 제한하지 않는다`() = runTest {
        coEvery { bookRepository.getFavoriteBooks("", "asc", 20_000, null, 1, 100) } returns emptyList()

        SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC, minPrice = 20_000))

        coVerify(exactly = 1) { bookRepository.getFavoriteBooks("", "asc", 20_000, null, 1, 100) }
    }

    @Test
    fun `최대 가격만 지정했을 때_즐겨찾기를 조회하면_최소 가격을 제한하지 않는다`() = runTest {
        coEvery { bookRepository.getFavoriteBooks("", "asc", null, 10_000, 1, 100) } returns emptyList()

        SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC, maxPrice = 10_000))

        coVerify(exactly = 1) { bookRepository.getFavoriteBooks("", "asc", null, 10_000, 1, 100) }
    }

    @Test
    fun `조회 결과가 없을 때_즐겨찾기를 조회하면_NoSearchResults를 반환한다`() = runTest {
        coEvery { bookRepository.getFavoriteBooks(any(), any(), any(), any(), any(), any()) } throws BookException.NoSearchResults()

        val result = SUT.invoke(param())

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.NoSearchResults)
    }

    @Test
    fun `즐겨찾기 조회가 실패할 때_즐겨찾기를 조회하면_Fail을 반환한다`() = runTest {
        val failure = IllegalStateException("Read failed")
        coEvery { bookRepository.getFavoriteBooks(any(), any(), any(), any(), any(), any()) } throws failure

        val result = SUT.invoke(param())

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.Fail(failure))
    }

    @Test
    fun `즐겨찾기 조회 중 취소되면_취소를 전파한다`() = runTest {
        val cancellation = CancellationException("Cancelled")
        coEvery { bookRepository.getFavoriteBooks(any(), any(), any(), any(), any(), any()) } throws cancellation

        val result = assertThrows<CancellationException> { SUT.invoke(param()) }

        assertThat(result).isSameInstanceAs(cancellation)
    }

    @Test
    fun `페이지가 1보다 작을 때_즐겨찾기를 조회하면_InvalidPage를 반환하고 조회하지 않는다`() = runTest {
        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC, page = 0))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.InvalidPage)
        coVerify(exactly = 0) { bookRepository.getFavoriteBooks(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `더 가져올 도서가 없을 때_다음 페이지를 조회하면_NoMoreBooks를 반환한다`() = runTest {
        coEvery { bookRepository.getFavoriteBooks(any(), any(), any(), any(), any(), any()) } throws BookException.NoMoreBooks()

        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC, page = 2))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.NoMoreBooks)
    }

    @Test
    fun `제목 검색어에 앞뒤 공백이 있을 때_즐겨찾기를 조회하면_공백을 제거하고 나머지 조회 조건을 유지한다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getFavoriteBooks("기록", "desc", 10_000, 20_000, 2, 100) } returns books

        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(
            sort = GetFavoriteBooksUseCase.Param.Sort.DESC,
            minPrice = 10_000,
            maxPrice = 20_000,
            page = 2,
            query = "  기록  ",
        ))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.Success(books))
    }

    @Test
    fun `검색어가 공백일 때_즐겨찾기를 조회하면_전체 조회 조건을 전달한다`() = runTest {
        val books = listOf(fixture<Book>())
        coEvery { bookRepository.getFavoriteBooks("", "asc", null, null, 1, 100) } returns books

        val result = SUT.invoke(GetFavoriteBooksUseCase.Param(
            sort = GetFavoriteBooksUseCase.Param.Sort.ASC,
            query = "  ",
        ))

        assertThat(result).isEqualTo(GetFavoriteBooksUseCase.Result.Success(books))
    }

    private fun param() = GetFavoriteBooksUseCase.Param(GetFavoriteBooksUseCase.Param.Sort.ASC)
}
