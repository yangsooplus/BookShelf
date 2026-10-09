package com.yangsooplus.bookshelf.data.book.repository

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

import com.appmattus.kotlinfixture.kotlinFixture
import com.yangsooplus.bookshelf.data.book.mapper.toBook
import com.yangsooplus.bookshelf.data.book.mapper.toFavoriteBookEntity
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookDao
import com.yangsooplus.bookshelf.data.datasource.network.book.BookApiService
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookDocument
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchMeta
import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookSearchResponse
import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class DefaultBookRepositoryTest {
    private val fixture = kotlinFixture()
    private val api = mockk<BookApiService>()
    private val dao = mockk<FavoriteBookDao> {
        coEvery { isFavorite(id = any()) } returns false
    }
    private val repository = DefaultBookRepository(bookApiService = api, favoriteBookDao = dao)

    @Test
    fun `검색 결과가 마지막 페이지에 있을 때_도서를 조회하면_요청 조건을 전달하고 도서를 반환한다`() = runBlocking {
        val document = fixture<BookDocument>().copy(isbn = "0132350882", datetime = "2026-10-09T00:00:00.000+09:00")
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 21, pageableCount = 21, isEnd = true), documents = listOf(document))
        coEvery { api.searchBooks(query = "코틀린", sort = "latest", page = 2, size = 20) } returns response

        val books = repository.getBooks(query = "코틀린", sort = "latest", page = 2, size = 20)

        assertEquals(listOf(document.toBook()), books)
        assertEquals("9780132350884", books.single().id)
    }

    @Test
    fun `ISBN이 없는 도서가 함께 응답될 때_도서를 조회하면_정상 도서를 반환한다`() = runBlocking {
        val valid = fixture<BookDocument>().copy(isbn = "0132350882", datetime = "2026-10-09T00:00:00.000+09:00")
        val response = BookSearchResponse(
            meta = BookSearchMeta(totalCount = 2, pageableCount = 2, isEnd = true),
            documents = listOf(valid.copy(isbn = null), valid),
        )
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 1, size = 20) } returns response
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        try {
            val books = repository.getBooks(query = "책", sort = "accuracy", page = 1, size = 20)

            assertEquals(listOf(valid.toBook()), books)
        } finally {
            unmockkStatic(Log::class)
        }
    }

    @Test
    fun `모든 응답 도서에 ISBN이 없을 때_도서를 조회하면_빈 목록을 반환한다`() = runBlocking<Unit> {
        val response = BookSearchResponse(
            meta = BookSearchMeta(totalCount = 1, pageableCount = 1, isEnd = true),
            documents = listOf(fixture<BookDocument>().copy(isbn = null)),
        )
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 2, size = 20) } returns response
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        try {
            val books = repository.getBooks(query = "책", sort = "accuracy", page = 2, size = 20)

            assertEquals(emptyList<Book>(), books)
        } finally {
            unmockkStatic(Log::class)
        }
    }

    @Test
    fun `검색된 도서가 없을 때_도서를 조회하면_NoSearchResults를 전달한다`() = runBlocking<Unit> {
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 0, pageableCount = 0, isEnd = true), documents = emptyList())
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 1, size = 20) } returns response

        expectFailure<BookException.NoSearchResults> {
            repository.getBooks(query = "책", sort = "accuracy", page = 1, size = 20)
        }
    }

    @Test
    fun `검색 결과 20권을 모두 조회한 뒤_2페이지에서 빈 응답을 받으면_NoMoreBooks를 전달한다`() = runBlocking<Unit> {
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 20, pageableCount = 20, isEnd = true), documents = emptyList())
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 2, size = 20) } returns response

        expectFailure<BookException.NoMoreBooks> {
            repository.getBooks(query = "책", sort = "accuracy", page = 2, size = 20)
        }
    }

    @Test
    fun `통신이 실패할 때_도서를 조회하면_오류를 전달한다`() = runBlocking {
        val failure = IOException("connection failed")
        coEvery { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) } throws failure

        val actual = expectFailure<IOException> { repository.getBooks(query = "책", sort = "accuracy", page = 1, size = 20) }

        assertSame(failure, actual)
    }

    @Test
    fun `조회가 취소됐을 때_원격과 로컬 도서를 조회하면_취소를 그대로 전달한다`() = runBlocking {
        val cancellation = CancellationException("cancelled")
        coEvery { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) } throws cancellation
        coEvery { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) } throws cancellation

        val remoteFailure = expectFailure<CancellationException> { repository.getBooks(query = "책", sort = "accuracy", page = 1, size = 20) }
        val localFailure = expectFailure<CancellationException> { repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = 1, size = 100) }

        assertSame(cancellation, remoteFailure)
        assertSame(cancellation, localFailure)
    }

    @Test
    fun `즐겨찾기가 저장되어 있을 때_조건과 페이지로 조회하면_DAO에 조건을 전달하고 상세 정보를 복원한다`() = runBlocking {
        val original = fixture<Book>()
        coEvery { dao.getFavoriteBooks(query = "책", ascending = false, minPrice = 0, maxPrice = 20000, limit = 100, offset = 200) } returns listOf(original.toFavoriteBookEntity())

        val books = repository.getFavoriteBooks(query = "책", sort = "desc", minPrice = 0, maxPrice = 20000, page = 3, size = 100)

        assertEquals(listOf(original.copy(isFavorite = true)), books)
        coVerify(exactly = 0) { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) }
    }

    @Test
    fun `조건에 맞는 즐겨찾기가 없을 때_첫 페이지를 조회하면_NoSearchResults를 전달한다`() = runBlocking<Unit> {
        coEvery { dao.getFavoriteBooks(query = "", ascending = true, minPrice = null, maxPrice = null, limit = 100, offset = 0) } returns emptyList()

        expectFailure<BookException.NoSearchResults> {
            repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = 1, size = 100)
        }
    }

    @Test
    fun `즐겨찾기 페이지가 끝났을 때_다음 페이지를 조회하면_NoMoreBooks를 전달한다`() = runBlocking<Unit> {
        coEvery { dao.getFavoriteBooks(query = "", ascending = true, minPrice = null, maxPrice = null, limit = 100, offset = 100) } returns emptyList()

        expectFailure<BookException.NoMoreBooks> {
            repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = 2, size = 100)
        }
    }

    @Test
    fun `같은 도서의 정보가 변경됐을 때_즐겨찾기를 다시 추가하면_같은 ID로 최신 정보를 저장하도록 요청한다`() = runBlocking {
        val original = fixture<Book>()
        val updated = original.copy(title = "변경 제목", contents = "변경 소개")
        coEvery { dao.upsert(book = any()) } returns Unit

        repository.setFavorite(book = original, isFavorite = true)
        repository.setFavorite(book = updated, isFavorite = true)

        coVerifySequence {
            dao.upsert(book = original.toFavoriteBookEntity())
            dao.upsert(book = updated.toFavoriteBookEntity())
        }
        coVerify(exactly = 0) { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) }
    }

    @Test
    fun `도서 정보가 바뀌었을 때_즐겨찾기를 반복 해제하면_ID로 삭제한다`() = runBlocking {
        val original = fixture<Book>()
        val changed = original.copy(title = "다른 제목", contents = "다른 소개")
        coEvery { dao.deleteById(id = original.id) } returns Unit

        repository.setFavorite(book = changed, isFavorite = false)
        repository.setFavorite(book = changed, isFavorite = false)

        coVerify(exactly = 2) { dao.deleteById(id = original.id) }
        coVerify(exactly = 0) { dao.upsert(book = any()) }
    }

    @Test
    fun `DB가 실패할 때_즐겨찾기를 조회하거나 변경하면_오류를 전달한다`() = runBlocking {
        val failure = IllegalStateException("database failed")
        val book = fixture<Book>()
        coEvery { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) } throws failure
        coEvery { dao.upsert(book = any()) } throws failure
        coEvery { dao.deleteById(id = any()) } throws failure

        val read = expectFailure<IllegalStateException> { repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = 1, size = 100) }
        val save = expectFailure<IllegalStateException> { repository.setFavorite(book = book, isFavorite = true) }
        val delete = expectFailure<IllegalStateException> { repository.setFavorite(book = book, isFavorite = false) }

        assertSame(failure, read)
        assertSame(failure, save)
        assertSame(failure, delete)
    }

    @Test
    fun `가격 범위가 잘못됐을 때_즐겨찾기를 조회하면_메시지를 포함한 인자 오류를 전달한다`() = runBlocking {
        val requests = listOf(-1 to null, null to -1, 20000 to 10000)

        val failures = requests.map { (min, max) ->
            expectFailure<BookException.InvalidArgument> {
                repository.getFavoriteBooks(query = "", sort = "asc", minPrice = min, maxPrice = max, page = 1, size = 100)
            }
        }

        assertEquals(listOf("Price bounds must be non-negative", "Price bounds must be non-negative", "Minimum price exceeds maximum price"), failures.map { it.message })
        coVerify(exactly = 0) { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) }
    }

    @Test
    fun `조회 크기나 정렬이 잘못됐을 때_즐겨찾기를 조회하면_인자 오류를 전달한다`() = runBlocking {
        val requests = listOf("asc" to 0, "latest" to 100)

        val failures = requests.map { (sort, size) ->
            expectFailure<BookException.InvalidArgument> {
                repository.getFavoriteBooks(query = "", sort = sort, minPrice = null, maxPrice = null, page = 1, size = size)
            }
        }

        assertEquals(listOf("Size must be positive", "Invalid favorite book sort: latest"), failures.map { it.message })
        coVerify(exactly = 0) { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) }
    }

    @Test
    fun `페이지 계산이 DB의 정수 범위를 넘을 때_즐겨찾기를 조회하면_페이지 오류를 전달한다`() = runBlocking {
        val page = Int.MAX_VALUE

        val failure = expectFailure<BookException.InvalidPage> {
            repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = page, size = 100)
        }

        assertEquals("Favorite book offset is too large", failure.message)
        coVerify(exactly = 0) { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) }
    }

    @Test
    fun `카카오의 허용 페이지 범위를 벗어났을 때_도서를 조회하면_API 호출 없이 페이지 오류를 전달한다`() = runBlocking {
        val pages = listOf(Int.MIN_VALUE, 0, 51, Int.MAX_VALUE)

        val failures = pages.map { page ->
            expectFailure<BookException.InvalidPage> {
                repository.getBooks(query = "책", sort = "accuracy", page = page, size = 20)
            }
        }

        assertEquals(List(pages.size) { "Kakao book search page must be between 1 and 50" }, failures.map { it.message })
        coVerify(exactly = 0) { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) }
    }

    @Test
    fun `카카오의 허용 조회 크기를 벗어났을 때_도서를 조회하면_API 호출 없이 인자 오류를 전달한다`() = runBlocking {
        val sizes = listOf(-1, 0, 51)

        val failures = sizes.map { size ->
            expectFailure<BookException.InvalidArgument> {
                repository.getBooks(query = "책", sort = "accuracy", page = 1, size = size)
            }
        }

        assertEquals(List(sizes.size) { "Kakao book search size must be between 1 and 50" }, failures.map { it.message })
        coVerify(exactly = 0) { api.searchBooks(query = any(), sort = any(), page = any(), size = any()) }
    }

    @Test
    fun `카카오의 페이지와 조회 크기가 경계값일 때_도서를 조회하면_API에 요청을 전달한다`() = runBlocking {
        val document = fixture<BookDocument>().copy(isbn = "0132350882", datetime = "2026-10-09T00:00:00.000+09:00")
        val response = BookSearchResponse(meta = BookSearchMeta(totalCount = 1000, pageableCount = 1000, isEnd = false), documents = listOf(document))
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = any(), size = any()) } returns response
        val requests = listOf(1 to 1, 1 to 50, 50 to 1, 50 to 50)

        val results = requests.map { (page, size) ->
            repository.getBooks(query = "책", sort = "accuracy", page = page, size = size)
        }

        assertEquals(List(requests.size) { listOf(document.toBook()) }, results)
        requests.forEach { (page, size) ->
            coVerify(exactly = 1) { api.searchBooks(query = "책", sort = "accuracy", page = page, size = size) }
        }
    }

    @Test
    fun `즐겨찾기 페이지가 양수가 아닐 때_조회하면_DB 호출 없이 페이지 오류를 전달한다`() = runBlocking {
        val pages = listOf(-1, 0)

        val failures = pages.map { page ->
            expectFailure<BookException.InvalidPage> {
                repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = page, size = 100)
            }
        }

        assertEquals(List(pages.size) { "Favorite book page must be positive" }, failures.map { it.message })
        coVerify(exactly = 0) { dao.getFavoriteBooks(query = any(), ascending = any(), minPrice = any(), maxPrice = any(), limit = any(), offset = any()) }
    }

    @Test
    fun `즐겨찾기 페이지가 50을 넘을 때_조회하면_카카오 제한 없이 DB에 요청을 전달한다`() = runBlocking {
        val book = fixture<Book>()
        coEvery { dao.getFavoriteBooks(query = "", ascending = true, minPrice = null, maxPrice = null, limit = 100, offset = 5000) } returns listOf(book.toFavoriteBookEntity())

        val books = repository.getFavoriteBooks(query = "", sort = "asc", minPrice = null, maxPrice = null, page = 51, size = 100)

        assertEquals(listOf(book.copy(isFavorite = true)), books)
    }

    @Test
    fun `다음 페이지에 저장한 도서가 있을 때_검색하면_DB 기준 즐겨찾기 여부를 채운다`() = runBlocking {
        val saved = fixture<BookDocument>().copy(isbn = "0132350882", datetime = "2026-10-09T00:00:00.000+09:00")
        val unsaved = saved.copy(isbn = "9780201633610")
        val response = BookSearchResponse(
            meta = BookSearchMeta(totalCount = 22, pageableCount = 22, isEnd = true),
            documents = listOf(saved, unsaved),
        )
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 2, size = 20) } returns response
        coEvery { dao.isFavorite(id = saved.toBook().id) } returns true

        val books = repository.getBooks(query = "책", sort = "accuracy", page = 2, size = 20)

        assertEquals(listOf(saved.toBook().copy(isFavorite = true), unsaved.toBook().copy(isFavorite = false)), books)
    }

    @Test
    fun `즐겨찾기 여부 조회가 실패했을 때_도서를 검색하면_오류를 전달한다`() = runBlocking {
        val document = fixture<BookDocument>().copy(isbn = "0132350882", datetime = "2026-10-09T00:00:00.000+09:00")
        val response = BookSearchResponse(
            meta = BookSearchMeta(totalCount = 1, pageableCount = 1, isEnd = true),
            documents = listOf(document),
        )
        val failure = IllegalStateException("database failed")
        coEvery { api.searchBooks(query = "책", sort = "accuracy", page = 1, size = 20) } returns response
        coEvery { dao.isFavorite(id = any()) } throws failure

        val actual = expectFailure<IllegalStateException> { repository.getBooks(query = "책", sort = "accuracy", page = 1, size = 20) }

        assertSame(failure, actual)
    }

    private suspend inline fun <reified T : Throwable> expectFailure(block: suspend () -> Unit): T {
        try {
            block()
        } catch (failure: Throwable) {
            if (failure is T) return failure
            throw failure
        }
        throw AssertionError("Expected ${T::class.simpleName}")
    }
}
