package com.yangsooplus.bookshelf.feature.book.screen.favorites

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import com.yangsooplus.bookshelf.domain.book.model.FavoriteMetaData
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import com.yangsooplus.bookshelf.domain.book.usecase.GetFavoriteBooksUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.GetFavoriteMetaDataUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteBooksViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeBookRepository()
    private lateinit var viewModel: FavoriteBooksViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = dispatcher)
        viewModel = FavoriteBooksViewModel(
            getFavoriteBooksUseCase = GetFavoriteBooksUseCase(bookRepository = repository),
            getFavoriteMetaDataUseCase = GetFavoriteMetaDataUseCase(bookRepository = repository),
            setFavoriteBookUseCase = SetFavoriteBookUseCase(bookRepository = repository),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `검색과 정렬을 적용했을 때_가격 필터를 적용하고 초기화하면_기존 조건을 유지하고 가격 조건만 변경한다`() = runTest(context = dispatcher) {
        repository.books = listOf(book(id = "1"))
        viewModel.intent(intent = FavoriteBooksIntent.EnterScreen)
        viewModel.intent(intent = FavoriteBooksIntent.Search(query = "기록"))
        viewModel.intent(intent = FavoriteBooksIntent.ChangeSort(sort = FavoriteBooksSort.TitleDescending))
        runCurrent()

        viewModel.intent(intent = FavoriteBooksIntent.ApplyPriceFilter(minPrice = 0, maxPrice = 10_000))
        runCurrent()
        assertEquals(Request(query = "기록", sort = "desc", minPrice = 0, maxPrice = 10_000, page = 1), repository.requests.last())
        viewModel.intent(intent = FavoriteBooksIntent.ResetPriceFilter)
        runCurrent()

        assertEquals(Request(query = "기록", sort = "desc", minPrice = null, maxPrice = null, page = 1), repository.requests.last())
        assertEquals(20_000, viewModel.state.value.priceRangeUpperBound)
    }

    @Test
    fun `다음 페이지까지 조회했을 때_즐겨찾기를 해제하면_변경된 목록과 개수를 첫 페이지부터 다시 조회한다`() = runTest(context = dispatcher) {
        repository.books = listOf(book(id = "1"), book(id = "2"))
        viewModel.intent(intent = FavoriteBooksIntent.EnterScreen)
        runCurrent()
        viewModel.intent(intent = FavoriteBooksIntent.LoadMore)
        runCurrent()

        viewModel.intent(intent = FavoriteBooksIntent.RemoveFavorite(book = book(id = "1")))
        runCurrent()

        assertEquals(listOf("2"), viewModel.state.value.books.map { it.id })
        assertEquals(1, viewModel.state.value.totalCount)
        assertEquals(1, repository.requests.last().page)
        assertEquals(false, repository.savedFavorite)
    }

    @Test
    fun `즐겨찾기 해제에 실패했을 때_해제를 요청하면_목록과 개수를 유지하고 실패 메시지를 전달한다`() = runTest(context = dispatcher) {
        repository.books = listOf(book(id = "1"))
        repository.failSave = true
        val effects = mutableListOf<FavoriteBooksEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects.add(element = it) } }
        viewModel.intent(intent = FavoriteBooksIntent.EnterScreen)
        runCurrent()

        viewModel.intent(intent = FavoriteBooksIntent.RemoveFavorite(book = book(id = "1")))
        runCurrent()

        assertEquals(listOf("1"), viewModel.state.value.books.map { it.id })
        assertEquals(1, viewModel.state.value.totalCount)
        assertTrue(effects.single() is FavoriteBooksEffect.ShowMessage)
    }

    @Test
    fun `즐겨찾기 조회가 늦어질 때_가격 필터 변경 후 이전 조회가 실패하면_새 결과와 상태를 유지한다`() = runTest(context = dispatcher) {
        val oldResponse = CompletableDeferred<List<Book>>()
        repository.search = { request ->
            if (request.maxPrice == null) {
                withContext(context = NonCancellable) { oldResponse.await() }
            } else {
                listOf(book(id = "필터 결과"))
            }
        }
        viewModel.intent(intent = FavoriteBooksIntent.Search(query = "기록"))
        runCurrent()

        viewModel.intent(intent = FavoriteBooksIntent.ApplyPriceFilter(minPrice = 0, maxPrice = 10_000))
        runCurrent()
        oldResponse.completeExceptionally(exception = IllegalStateException("이전 조회 실패"))
        runCurrent()

        assertEquals(listOf("필터 결과"), viewModel.state.value.books.map { it.id })
        assertEquals(10_000, viewModel.state.value.maxPriceFilter)
        assertEquals(FavoriteBooksState.FavoriteBooksStatus.Results, viewModel.state.value.status)
    }

    private data class Request(val query: String, val sort: String, val minPrice: Int?, val maxPrice: Int?, val page: Int)

    private class FakeBookRepository : BookRepository {
        var books: List<Book> = emptyList()
        var search: (suspend (Request) -> List<Book>)? = null
        var failSave = false
        var savedFavorite: Boolean? = null
        val requests = mutableListOf<Request>()

        override suspend fun getFavoriteBooks(query: String, sort: String, minPrice: Int?, maxPrice: Int?, page: Int, size: Int): List<Book> {
            val request = Request(query = query, sort = sort, minPrice = minPrice, maxPrice = maxPrice, page = page)
            requests.add(element = request)
            search?.let { return it(request) }
            if (books.isEmpty()) throw BookException.NoSearchResults()
            return books
        }

        override suspend fun getFavoriteMetaData(): FavoriteMetaData = FavoriteMetaData(totalCount = books.size, maxPrice = 16_000)

        override suspend fun setFavorite(book: Book, isFavorite: Boolean) {
            if (failSave) error("삭제 실패")
            savedFavorite = isFavorite
            books = books.filterNot { it.id == book.id }
        }

        override suspend fun getBooks(query: String, sort: String, page: Int, size: Int): List<Book> = error("네트워크 조회는 사용하지 않는다")
    }

    private fun book(id: String) = Book(
        id = id,
        title = "기록",
        contents = "",
        url = "",
        authors = emptyList(),
        publisher = "",
        publishedDate = LocalDate.MIN,
        price = BookPrice(regularPrice = 16_000),
        thumbnailUrl = "",
        isbn = id,
        translators = emptyList(),
        isFavorite = true,
    )
}
