package com.yangsooplus.bookshelf.data.datasource.database

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookDao
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookEntity
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteBookDaoTest {
    private lateinit var context: Context
    private lateinit var database: BookShelfDatabase
    private lateinit var dao: FavoriteBookDao
    private val databaseName = "favorites-test-${UUID.randomUUID()}.db"

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        openDatabase()
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    private fun openDatabase() {
        database = Room.databaseBuilder(context, BookShelfDatabase::class.java, databaseName).build()
        dao = database.favoriteBookDao()
    }

    @Test
    fun `저자와 번역자 목록 및 날짜가 있을 때_도서를 저장하고 조회하면_모든 상세 정보를 보존한다`() = runBlocking {
        val original = book(id = "1").copy(
            authors = listOf("김, 작가", "이\"작가", "줄\n바꿈", ""),
            translators = listOf("역자, 하나", "역자 둘"),
            publishedDate = LocalDate.of(1960, 2, 29),
        )
        dao.upsert(book = original)
        assertEquals(original, dao.getById(id = "1"))
        val emptyNames = book(id = "2").copy(authors = emptyList(), translators = emptyList())
        dao.upsert(book = emptyNames)
        assertEquals(emptyNames, dao.getById(id = "2"))
    }

    @Test
    fun `같은 ID의 도서를 반복 저장하고 삭제할 때_즐겨찾기를 조회하면_갱신과 삭제를 반영하고 다른 도서를 보존한다`() = runBlocking {
        dao.upsert(book = book(id = "1"))
        val updated = book(id = "1", title = "변경된 제목").copy(contents = "새 소개", salePrice = 0)
        dao.upsert(book = updated)
        assertEquals(listOf(updated), find())
        dao.upsert(book = book(id = "2"))
        dao.deleteById(id = "1")
        dao.deleteById(id = "1")
        assertNull(dao.getById(id = "1"))
        assertEquals(listOf(book(id = "2")), find())
    }

    @Test
    fun `즐겨찾기가 저장되어 있을 때_DB를 닫고 다시 열면_저장된 도서를 유지한다`() = runBlocking {
        val original = book(id = "1")
        dao.upsert(book = original)
        database.close()
        openDatabase()
        assertEquals(original, dao.getById(id = "1"))
    }

    @Test
    fun `판매가와 정가가 다양할 때_가격 범위로 조회하면_유효한 판매가를 우선하고 경계값과 무료 도서를 포함한다`() = runBlocking {
        val books = listOf(
            book(id = "free", title = "a").copy(salePrice = 0),
            book(id = "min", title = "b").copy(regularPrice = 30000, salePrice = 10000),
            book(id = "max", title = "c").copy(regularPrice = 20000, salePrice = null),
            book(id = "invalid-sale", title = "d").copy(regularPrice = 15000, salePrice = -1),
            book(id = "unknown", title = "e").copy(regularPrice = -1, salePrice = null),
            book(id = "invalid", title = "f").copy(regularPrice = -1, salePrice = -1),
            book(id = "outside", title = "g").copy(regularPrice = 10000, salePrice = 20001),
        )
        books.forEach { dao.upsert(book = it) }
        assertEquals(listOf("min", "max", "invalid-sale"), find(min = 10000, max = 20000).map { it.id })
        assertEquals(listOf("free"), find(min = 0, max = 0).map { it.id })
        assertEquals(7, find().size)
        assertEquals(listOf("free", "min", "max", "invalid-sale"), find(max = 20000).map { it.id })
        assertEquals(listOf("max", "outside"), find(min = 20000).map { it.id })
    }

    @Test
    fun `같은 제목과 특수문자를 포함한 도서가 있을 때_검색하고 페이지로 조회하면_문자 그대로 검색하고 일정한 순서로 반환한다`() = runBlocking {
        listOf(
            book(id = "2", title = "같은 제목"), book(id = "1", title = "같은 제목"), book(id = "3", title = "다른 책"),
            book(id = "4", title = "100%_Kotlin"), book(id = "5", title = "100abcKotlin"),
        ).forEach { dao.upsert(book = it) }
        assertEquals(listOf("1", "2"), find(query = "같은").map { it.id })
        assertEquals(listOf("4"), find(query = "%_").map { it.id })
        assertEquals(listOf("4", "5"), find(query = "kotlin").map { it.id })
        val all = find()
        assertEquals(all.reversed(), find(ascending = false))
        assertEquals(listOf("2", "1"), find(query = "같은", ascending = false).map { it.id })
        assertEquals(all.take(2), find(limit = 2))
        assertEquals(all.drop(2).take(2), find(limit = 2, offset = 2))
        assertEquals(emptyList<FavoriteBookEntity>(), find(offset = 5))
    }

    @Test
    fun `정가 없이 판매가만 있는 도서가 있을 때_즐겨찾기 정보를 조회하면_판매가로 최대 가격을 계산한다`() = runBlocking {
        dao.upsert(book = book(id = "sale-only").copy(regularPrice = -1, salePrice = 25_000))
        dao.upsert(book = book(id = "unknown").copy(regularPrice = -1, salePrice = null))

        val metaData = dao.getFavoriteMetaData()

        assertEquals(2, metaData.totalCount)
        assertEquals(25_000, metaData.maxPrice)
        assertEquals(listOf("sale-only"), find(min = 0, max = metaData.maxPrice).map { it.id })
    }

    @Test
    fun `정가와 판매가가 모두 있는 도서가 있을 때_즐겨찾기 정보를 조회하면_정가를 우선해 최대 가격을 계산한다`() = runBlocking {
        dao.upsert(book = book(id = "regular").copy(regularPrice = 30_000, salePrice = 20_000))
        dao.upsert(book = book(id = "sale-only").copy(regularPrice = -1, salePrice = 25_000))

        val metaData = dao.getFavoriteMetaData()

        assertEquals(2, metaData.totalCount)
        assertEquals(30_000, metaData.maxPrice)
    }

    private suspend fun find(
        query: String = "",
        ascending: Boolean = true,
        min: Int? = null,
        max: Int? = null,
        limit: Int = 100,
        offset: Int = 0,
    ) = dao.getFavoriteBooks(query = query, ascending = ascending, minPrice = min, maxPrice = max, limit = limit, offset = offset)

    private fun book(id: String, title: String = "책 제목") = FavoriteBookEntity(
        id = id,
        title = title,
        contents = "책 소개",
        url = "https://example.com/books/$id",
        authors = listOf("저자"),
        publisher = "출판사",
        publishedDate = LocalDate.of(2026, 10, 9),
        regularPrice = 16000,
        salePrice = 14400,
        thumbnailUrl = "https://example.com/covers/$id.jpg",
        isbn = "9780000000000 0000000000",
        translators = emptyList(),
    )
}
