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
    fun storesAllDetailFieldsWithoutLosingNamesOrDates() = runBlocking {
        val original = book("1").copy(
            authors = listOf("김, 작가", "이\"작가", "줄\n바꿈", ""),
            translators = listOf("역자, 하나", "역자 둘"),
            publishedDate = LocalDate.of(1960, 2, 29),
        )
        dao.upsert(original)
        assertEquals(original, dao.getById("1"))
        val emptyNames = book("2").copy(authors = emptyList(), translators = emptyList())
        dao.upsert(emptyNames)
        assertEquals(emptyNames, dao.getById("2"))
    }

    @Test
    fun repeatedSaveUpdatesOneRowAndRepeatedDeleteIsSafe() = runBlocking {
        dao.upsert(book("1"))
        val updated = book("1", "변경된 제목").copy(contents = "새 소개", salePrice = 0)
        dao.upsert(updated)
        assertEquals(listOf(updated), find())
        dao.upsert(book("2"))
        dao.deleteById("1")
        dao.deleteById("1")
        assertNull(dao.getById("1"))
        assertEquals(listOf(book("2")), find())
    }

    @Test
    fun dataSurvivesClosingAndReopeningDatabase() = runBlocking {
        val original = book("1")
        dao.upsert(original)
        database.close()
        openDatabase()
        assertEquals(original, dao.getById("1"))
    }

    @Test
    fun priceBoundsAreInclusiveAndUseValidSalePriceBeforeRegularPrice() = runBlocking {
        val books = listOf(
            book("free", "a").copy(salePrice = 0),
            book("min", "b").copy(regularPrice = 30000, salePrice = 10000),
            book("max", "c").copy(regularPrice = 20000, salePrice = null),
            book("invalid-sale", "d").copy(regularPrice = 15000, salePrice = -1),
            book("unknown", "e").copy(regularPrice = -1, salePrice = null),
            book("invalid", "f").copy(regularPrice = -1, salePrice = -1),
            book("outside", "g").copy(regularPrice = 10000, salePrice = 20001),
        )
        books.forEach { dao.upsert(it) }
        assertEquals(listOf("min", "max", "invalid-sale"), find(min = 10000, max = 20000).map { it.id })
        assertEquals(listOf("free"), find(min = 0, max = 0).map { it.id })
        assertEquals(7, find().size)
        assertEquals(listOf("free", "min", "max", "invalid-sale"), find(max = 20000).map { it.id })
        assertEquals(listOf("max", "outside"), find(min = 20000).map { it.id })
    }

    @Test
    fun titleSearchUsesLiteralSubstringAndPaginationHasStableOrder() = runBlocking {
        listOf(
            book("2", "같은 제목"), book("1", "같은 제목"), book("3", "다른 책"),
            book("4", "100%_Kotlin"), book("5", "100abcKotlin"),
        ).forEach { dao.upsert(it) }
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

    private suspend fun find(
        query: String = "",
        ascending: Boolean = true,
        min: Int? = null,
        max: Int? = null,
        limit: Int = 100,
        offset: Int = 0,
    ) = dao.getFavoriteBooks(query, ascending, min, max, limit, offset)

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
