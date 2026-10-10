package com.yangsooplus.bookshelf.feature.book.screen.search

import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import com.yangsooplus.bookshelf.domain.book.model.FavoriteMetaData
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import com.yangsooplus.bookshelf.domain.book.usecase.GetBooksUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchPageStatus
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchSort
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
class BookSearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeBookRepository()
    private lateinit var viewModel: BookSearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = dispatcher)
        viewModel = BookSearchViewModel(
            getBooksUseCase = GetBooksUseCase(bookRepository = repository),
            setFavoriteBookUseCase = SetFavoriteBookUseCase(bookRepository = repository),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `검색 후 입력값을 변경했을 때_더 보기를 실행하면_기존 검색어의 다음 페이지를 중복 없이 추가한다`() = runTest(context = dispatcher) {
        repository.search = { _, page ->
            if (page == 1) listOf(book(id = "1")) else listOf(book(id = "1"), book(id = "2"))
        }
        viewModel.intent(intent = BookSearchIntent.Search(query = "기록"))
        runCurrent()
        viewModel.intent(intent = BookSearchIntent.ChangeQuery(query = "다른 입력"))
        runCurrent()

        viewModel.intent(intent = BookSearchIntent.LoadMore)
        runCurrent()

        assertEquals("기록" to 2, repository.requests.last())
        assertEquals("다른 입력", viewModel.state.value.query)
        assertEquals(listOf("1", "2"), viewModel.state.value.books.map { it.id })
    }

    @Test
    fun `즐겨찾기 저장이 진행 중일 때_저장에 실패하면_실패 전후 모두 기존 표시를 유지한다`() = runTest(context = dispatcher) {
        val effects = mutableListOf<BookSearchEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects.add(element = it) } }
        val saveResponse = CompletableDeferred<Unit>()
        repository.save = { saveResponse.await() }
        repository.search = { _, _ -> listOf(book(id = "1")) }
        viewModel.intent(intent = BookSearchIntent.Search(query = "기록"))
        runCurrent()

        viewModel.intent(intent = BookSearchIntent.ToggleFavorite(book = book(id = "1")))
        runCurrent()
        assertFalse(viewModel.state.value.books.first().isFavorite)
        saveResponse.completeExceptionally(exception = IllegalStateException("저장 실패"))
        runCurrent()

        assertFalse(viewModel.state.value.books.first().isFavorite)
        assertEquals(1, effects.size)
        assertTrue(effects.single() is BookSearchEffect.ShowMessage)
    }

    @Test
    fun `도서의 즐겨찾기 상태를 변경했을 때_도서를 선택하면_최신 도서를 상세 이동 Effect로 전달한다`() = runTest(context = dispatcher) {
        val effects = mutableListOf<BookSearchEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects.add(element = it) } }
        repository.search = { _, _ -> listOf(book(id = "1")) }
        viewModel.intent(intent = BookSearchIntent.Search(query = "기록"))
        runCurrent()
        viewModel.intent(intent = BookSearchIntent.ToggleFavorite(book = book(id = "1")))
        runCurrent()

        viewModel.intent(intent = BookSearchIntent.ClickBook(book = book(id = "1")))
        runCurrent()

        val effect = effects.filterIsInstance<BookSearchEffect.Navigation.OpenDetail>().single()
        assertTrue(effect.book.isFavorite)
    }

    @Test
    fun `다음 페이지 조회에 실패했을 때_다시 시도하면_기존 목록을 유지하고 같은 페이지를 조회한다`() = runTest(context = dispatcher) {
        repository.search = { _, page ->
            if (page == 1) listOf(book(id = "1")) else error("조회 실패")
        }
        viewModel.intent(intent = BookSearchIntent.Search(query = "기록"))
        runCurrent()
        viewModel.intent(intent = BookSearchIntent.LoadMore)
        runCurrent()
        assertEquals(listOf("1"), viewModel.state.value.books.map { it.id })
        assertEquals(BookSearchPageStatus.Error, viewModel.state.value.pageStatus)
        repository.search = { _, _ -> listOf(book(id = "2")) }

        viewModel.intent(intent = BookSearchIntent.LoadMore)
        runCurrent()

        assertEquals("기록" to 2, repository.requests.last())
        assertEquals(listOf("1", "2"), viewModel.state.value.books.map { it.id })
    }

    @Test
    fun `검색 결과가 있을 때_정렬을 변경하면_기존 목록을 비우고 새 결과만 표시한다`() = runTest(context = dispatcher) {
        repository.search = { _, _ -> listOf(book(id = "이전 결과")) }
        viewModel.intent(intent = BookSearchIntent.Search(query = "기록"))
        runCurrent()
        val sortedResponse = CompletableDeferred<List<Book>>()
        repository.search = { _, _ -> sortedResponse.await() }

        viewModel.intent(intent = BookSearchIntent.ChangeSort(sort = BookSearchSort.PublishedDate))
        runCurrent()
        assertTrue(viewModel.state.value.books.isEmpty())
        sortedResponse.complete(value = listOf(book(id = "새 결과")))
        runCurrent()

        assertEquals(listOf("새 결과"), viewModel.state.value.books.map { it.id })
        assertEquals(1, viewModel.state.value.page)
    }

    @Test
    fun `추가 페이지 조회가 늦어질 때_새 검색 후 이전 응답이 도착하면_새 검색 결과만 유지한다`() = runTest(context = dispatcher) {
        val oldResponse = CompletableDeferred<List<Book>>()
        repository.search = { query, page ->
            when {
                query == "새 검색" -> listOf(book(id = "새 결과"))
                page == 1 -> listOf(book(id = "이전 첫 페이지"))
                else -> withContext(context = NonCancellable) { oldResponse.await() }
            }
        }
        viewModel.intent(intent = BookSearchIntent.Search(query = "이전 검색"))
        runCurrent()
        viewModel.intent(intent = BookSearchIntent.LoadMore)
        runCurrent()

        viewModel.intent(intent = BookSearchIntent.Search(query = "새 검색"))
        runCurrent()
        oldResponse.complete(value = listOf(book(id = "이전 추가 페이지")))
        runCurrent()

        assertEquals("새 검색", viewModel.state.value.searchedQuery)
        assertEquals(listOf("새 결과"), viewModel.state.value.books.map { it.id })
        assertEquals(1, viewModel.state.value.page)
    }

    @Test
    fun `즐겨찾기 저장이 진행 중일 때_새 검색을 실행하면_저장은 취소되지 않고 성공 결과를 반영한다`() = runTest(context = dispatcher) {
        val saveResponse = CompletableDeferred<Unit>()
        repository.save = { saveResponse.await() }
        repository.search = { _, _ -> listOf(book(id = "1")) }
        viewModel.intent(intent = BookSearchIntent.Search(query = "이전 검색"))
        runCurrent()
        viewModel.intent(intent = BookSearchIntent.ToggleFavorite(book = book(id = "1")))
        runCurrent()

        viewModel.intent(intent = BookSearchIntent.Search(query = "새 검색"))
        runCurrent()
        saveResponse.complete(value = Unit)
        runCurrent()

        assertEquals("새 검색", viewModel.state.value.searchedQuery)
        assertTrue(viewModel.state.value.books.single().isFavorite)
    }

    private class FakeBookRepository : BookRepository {
        var search: suspend (String, Int) -> List<Book> = { _, _ -> emptyList() }
        var save: suspend () -> Unit = {}
        val requests = mutableListOf<Pair<String, Int>>()

        override suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book> {
            requests.add(element = query to page)
            return search(query, page)
        }

        override suspend fun setFavorite(book: Book, isFavorite: Boolean) = save()

        override suspend fun getFavoriteBooks(
            query: String, sort: String, minPrice: Int?, maxPrice: Int?, page: Int, size: Int,
        ): List<Book> = error("사용하지 않는 호출")

        override suspend fun getFavoriteMetaData(): FavoriteMetaData = error("사용하지 않는 호출")
    }

    private fun book(id: String) = Book(
        id = id,
        title = "도서",
        contents = "",
        url = "",
        authors = emptyList(),
        publisher = "",
        publishedDate = LocalDate.MIN,
        price = BookPrice(regularPrice = 10_000),
        thumbnailUrl = "",
        isbn = id,
        translators = emptyList(),
    )
}
