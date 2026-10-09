package com.yangsooplus.bookshelf.data.datasource.network.book

import com.yangsooplus.bookshelf.data.datasource.network.book.model.BookDocument
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class BookApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: BookApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BookApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `검색어와 기본 정렬 및 20개 페이지 크기를 전송한다`() = runBlocking {
        server.enqueue(MockResponse().setBody(EMPTY_RESPONSE))
        val response = service.searchBooks(query = "코틀린 & Android")
        val request = requireNotNull(server.takeRequest(1, TimeUnit.SECONDS))
        val url = requireNotNull(request.requestUrl)
        assertEquals("GET", request.method)
        assertEquals("/v3/search/book", url.encodedPath)
        assertEquals("코틀린 & Android", url.queryParameter("query"))
        assertEquals("accuracy", url.queryParameter("sort"))
        assertEquals("1", url.queryParameter("page"))
        assertEquals("20", url.queryParameter("size"))
        assertTrue(response.meta.isEnd)
        assertTrue(response.documents.isEmpty())
    }

    @Test
    fun `발간일 정렬과 다음 페이지를 전송한다`() = runBlocking {
        server.enqueue(MockResponse().setBody(EMPTY_RESPONSE))
        service.searchBooks(query = "도서", sort = "latest", page = 2, size = 20)
        val url = requireNotNull(server.takeRequest(1, TimeUnit.SECONDS)?.requestUrl)
        assertEquals("latest", url.queryParameter("sort"))
        assertEquals("2", url.queryParameter("page"))
    }

    @Test
    fun `정상 응답과 빈 값 및 추가 필드를 원본 그대로 파싱한다`() = runBlocking {
        server.enqueue(MockResponse().setBody(BOOK_RESPONSE))
        val response = service.searchBooks(query = "도서")
        assertEquals(1, response.meta.totalCount)
        assertEquals(1, response.meta.pageableCount)
        assertFalse(response.meta.isEnd)
        val book = response.documents.first()
        assertEquals("도서", book.title)
        assertEquals("소개", book.contents)
        assertEquals("https://example.com/book", book.url)
        assertEquals("8996991341 9788996991342", book.isbn)
        assertEquals("2014-11-17T00:00:00.000+09:00", book.datetime)
        assertEquals(listOf("저자"), book.authors)
        assertEquals("출판사", book.publisher)
        assertEquals(emptyList<String>(), book.translators)
        assertEquals(14900, book.price)
        assertEquals(-1, book.salePrice)
        assertEquals("", book.thumbnail)
        assertEquals("정상판매", book.status)
    }

    @Test
    fun `도서 필드가 누락되면 기본값 없이 null로 파싱한다`() = runBlocking {
        val document = json.parseToJsonElement(BOOK_RESPONSE).jsonObject
            .getValue("documents").let { it as JsonArray }.first().jsonObject
        for (field in document.keys - "new_field") {
            assertDocumentFieldIsNull(JsonObject(document - field), field)
        }
    }

    @Test
    fun `도서 필드가 null이면 그대로 파싱한다`() = runBlocking {
        val document = json.parseToJsonElement(BOOK_RESPONSE).jsonObject
            .getValue("documents").let { it as JsonArray }.first().jsonObject
        for (field in document.keys - "new_field") {
            assertDocumentFieldIsNull(JsonObject(document + (field to JsonNull)), field)
        }
    }

    private suspend fun assertDocumentFieldIsNull(document: JsonObject, field: String) {
        val response = json.parseToJsonElement(BOOK_RESPONSE).jsonObject
        val partialResponse = JsonObject(response + ("documents" to JsonArray(listOf(document))))
        server.enqueue(MockResponse().setBody(partialResponse.toString()))
        val book = service.searchBooks(query = "도서").documents.single()
        val decoded = Json.encodeToJsonElement(BookDocument.serializer(), book).jsonObject
        assertEquals(field, JsonNull, decoded.getValue(field))
    }

    @Test
    fun `페이징 메타데이터가 누락되면 파싱 오류를 전달한다`() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"documents":[]}"""))
        try {
            service.searchBooks(query = "도서")
            fail("SerializationException expected")
        } catch (_: SerializationException) {
        }
    }

    @Test
    fun `HTTP 인증 실패는 빈 결과로 변환하지 않고 전달한다`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(401).setBody("{}"))
        try {
            service.searchBooks(query = "도서")
            fail("HttpException expected")
        } catch (exception: HttpException) {
            assertEquals(401, exception.code())
        }
    }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        val BOOK_RESPONSE = """
            {
              "meta": {"total_count": 1, "pageable_count": 1, "is_end": false},
              "documents": [
                {
                  "title": "도서", "contents": "소개", "url": "https://example.com/book",
                  "isbn": "8996991341 9788996991342", "datetime": "2014-11-17T00:00:00.000+09:00",
                  "authors": ["저자"], "publisher": "출판사", "translators": [],
                  "price": 14900, "sale_price": -1, "thumbnail": "", "status": "정상판매",
                  "new_field": "ignored"
                }
              ]
            }
        """.trimIndent()

        const val EMPTY_RESPONSE =
            """{"meta":{"total_count":0,"pageable_count":0,"is_end":true},"documents":[]}"""
    }
}
