package com.yangsooplus.bookshelf.domain.book.usecase

import com.appmattus.kotlinfixture.kotlinFixture
import com.google.common.truth.Truth.assertThat
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SetFavoriteBookUseCaseTest {
    private lateinit var SUT: SetFavoriteBookUseCase
    @MockK
    private lateinit var bookRepository: BookRepository

    private val fixture = kotlinFixture()

    @BeforeAll
    fun setup() {
        MockKAnnotations.init(this)
        SUT = SetFavoriteBookUseCase(bookRepository = bookRepository)
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(bookRepository)
    }

    @Test
    fun `즐겨찾기 저장 또는 해제를 요청하면_요청한 상태를 전달하고 Success를 반환한다`() = runTest {
        val book = fixture<Book>()
        coEvery { bookRepository.setFavorite(book, any()) } returns Unit

        for (isFavorite in listOf(true, false)) {
            val result = SUT.invoke(SetFavoriteBookUseCase.Param(book, isFavorite))

            assertThat(result).isEqualTo(SetFavoriteBookUseCase.Result.Success)
            coVerify(exactly = 1) { bookRepository.setFavorite(book, isFavorite) }
        }
    }

    @Test
    fun `즐겨찾기 저장이 실패할 때_즐겨찾기를 저장하면_Fail을 반환한다`() = runTest {
        val book = fixture<Book>()
        val failure = IllegalStateException("Write failed")
        coEvery { bookRepository.setFavorite(any(), any()) } throws failure

        val result = SUT.invoke(SetFavoriteBookUseCase.Param(book, true))

        assertThat(result).isEqualTo(SetFavoriteBookUseCase.Result.Fail(failure))
    }

    @Test
    fun `즐겨찾기 저장 중 취소되면_취소를 전파한다`() = runTest {
        val book = fixture<Book>()
        val cancellation = CancellationException("Cancelled")
        coEvery { bookRepository.setFavorite(any(), any()) } throws cancellation

        val result = assertThrows<CancellationException> { SUT.invoke(SetFavoriteBookUseCase.Param(book, true)) }

        assertThat(result).isSameInstanceAs(cancellation)
    }
}
