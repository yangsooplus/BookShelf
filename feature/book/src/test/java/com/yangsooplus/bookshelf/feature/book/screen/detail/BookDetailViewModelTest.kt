package com.yangsooplus.bookshelf.feature.book.screen.detail

import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import com.yangsooplus.bookshelf.domain.book.model.FavoriteMetaData
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeBookRepository()
    private lateinit var viewModel: BookDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = dispatcher)
        viewModel = BookDetailViewModel(setFavoriteBookUseCase = SetFavoriteBookUseCase(bookRepository = repository))
        viewModel.intent(intent = BookDetailIntent.Init(book = book()))
        dispatcher.scheduler.runCurrent()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `즐겨찾기 저장이 진행 중일 때_저장에 성공하면_성공 후 하트와 목록 전달 결과를 함께 갱신한다`() = runTest(context = dispatcher) {
        val response = CompletableDeferred<Unit>()
        repository.save = { response.await() }
        val effects = mutableListOf<BookDetailEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects.add(element = it) } }

        viewModel.intent(intent = BookDetailIntent.ToggleFavorite)
        runCurrent()
        assertFalse(viewModel.state.value.book!!.isFavorite)
        assertTrue(effects.isEmpty())
        response.complete(value = Unit)
        runCurrent()

        assertTrue(viewModel.state.value.book!!.isFavorite)
        assertEquals(viewModel.state.value.book, (effects.single() as BookDetailEffect.UpdateFavorite).book)
    }

    @Test
    fun `즐겨찾기 저장에 실패했을 때_즐겨찾기를 누르면_도서를 유지하고 실패 메시지만 전달한다`() = runTest(context = dispatcher) {
        repository.save = { error("저장 실패") }
        val effects = mutableListOf<BookDetailEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects.add(element = it) } }

        viewModel.intent(intent = BookDetailIntent.ToggleFavorite)
        runCurrent()

        assertEquals(book(), viewModel.state.value.book)
        assertTrue(effects.single() is BookDetailEffect.ShowMessage)
    }

    @Test
    fun `목록에서 즐겨찾기가 변경됐을 때_변경 결과를 전달하면_같은 도서의 하트만 갱신하고 저장 요청은 하지 않는다`() = runTest(context = dispatcher) {
        repository.save = { error("저장 요청은 발생하면 안 된다") }

        viewModel.intent(intent = BookDetailIntent.UpdateFavorite(book = book().copy(id = "다른 책", isFavorite = true)))
        runCurrent()
        assertFalse(viewModel.state.value.book!!.isFavorite)
        viewModel.intent(intent = BookDetailIntent.UpdateFavorite(book = book().copy(isFavorite = true)))
        runCurrent()

        assertEquals(book().copy(isFavorite = true), viewModel.state.value.book)
    }

    private class FakeBookRepository : BookRepository {
        var save: suspend () -> Unit = {}
        override suspend fun setFavorite(book: Book, isFavorite: Boolean) = save()
        override suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book> = error("추가 API 조회는 사용하지 않는다")
        override suspend fun getFavoriteBooks(query: String, sort: String, minPrice: Int?, maxPrice: Int?, page: Int, size: Int): List<Book> = error("사용하지 않는 호출")
        override suspend fun getFavoriteMetaData(): FavoriteMetaData = error("사용하지 않는 호출")
    }

    private fun book() = Book(
        id = "1", title = "도서", contents = "책 소개", url = "", authors = listOf("작가"), publisher = "출판사",
        publishedDate = LocalDate.MIN, price = BookPrice(regularPrice = 16_000), thumbnailUrl = "", isbn = "1", translators = emptyList(),
    )
}
