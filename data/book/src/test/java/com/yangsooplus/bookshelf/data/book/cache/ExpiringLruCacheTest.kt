package com.yangsooplus.bookshelf.data.book.cache

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ExpiringLruCacheTest {
    @Test
    fun `만료 전에 여러 번 사용했을 때_최초 저장의 유효 시간이 지나면_캐시를 반환하지 않는다`() {
        var now = 0L
        val cache = ExpiringLruCache<String, String>(maxEntries = 2, ttlMillis = 100, nowMillis = { now })
        cache.getOrPut(key = "검색", createValue = { "응답" })
        now = 99
        assertEquals("응답", cache.get(key = "검색"))

        now = 100
        val value = cache.get(key = "검색")

        assertNull(value)
    }

    @Test
    fun `최대 개수를 채운 뒤 기존 항목을 다시 사용했을 때_새 항목을 저장하면_가장 오래 사용하지 않은 항목을 제거한다`() {
        val cache = ExpiringLruCache<String, String>(maxEntries = 2, ttlMillis = 100, nowMillis = { 0 })
        cache.getOrPut(key = "A", createValue = { "A" })
        cache.getOrPut(key = "B", createValue = { "B" })
        cache.get(key = "A")

        cache.getOrPut(key = "C", createValue = { "C" })

        assertEquals("A", cache.get(key = "A"))
        assertNull(cache.get(key = "B"))
        assertEquals("C", cache.get(key = "C"))
    }

    @Test
    fun `최근에 사용한 항목이 만료됐을 때_새 항목을 저장하면_만료 항목을 먼저 정리하고 유효한 항목은 유지한다`() {
        var now = 0L
        val cache = ExpiringLruCache<String, String>(maxEntries = 2, ttlMillis = 100, nowMillis = { now })
        cache.getOrPut(key = "A", createValue = { "A" })
        now = 50
        cache.getOrPut(key = "B", createValue = { "B" })
        cache.get(key = "A")

        now = 100
        cache.getOrPut(key = "C", createValue = { "C" })

        assertNull(cache.get(key = "A"))
        assertEquals("B", cache.get(key = "B"))
        assertEquals("C", cache.get(key = "C"))
    }

    @Test
    fun `캐시 값에 데이터를 추가했을 때_다시 가져오면_같은 값을 반환하되 최초 유효 시간은 연장하지 않는다`() {
        var now = 0L
        val cache = ExpiringLruCache<String, MutableMap<Int, String>>(maxEntries = 2, ttlMillis = 100, nowMillis = { now })
        val pages = cache.getOrPut(key = "검색", createValue = { mutableMapOf(1 to "첫 페이지") })
        now = 90
        cache.getOrPut(key = "검색", createValue = { error("기존 값이 있으므로 실행되면 안 된다") })[2] = "두 번째 페이지"

        val cached = cache.get(key = "검색")

        assertSame(pages, cached)
        assertEquals(mapOf(1 to "첫 페이지", 2 to "두 번째 페이지"), cached)
        now = 100
        assertNull(cache.get(key = "검색"))
    }
}
