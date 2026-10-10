package com.yangsooplus.bookshelf.data.book.cache

/**
 * 최초 저장 시점부터 유효 시간을 계산하는 키·값 메모리 캐시.
 * 조회해도 유효 시간은 연장되지 않으며, 용량을 초과하면 가장 오래 사용하지 않은 항목부터 제거한다.
 * 만료된 항목은 조회하거나 새 항목을 저장할 때 정리한다.
 *
 * @property maxEntries 보관할 최대 항목 수. 기본값은 5개이며, 0보다 커야 한다.
 * @property ttlMillis 최초 저장부터 항목이 만료되기까지의 시간(밀리초). 기본값은 5분이며, 0보다 커야 한다.
 * @property nowMillis 현재 경과 시간을 밀리초로 반환하는 함수. 시스템 시각 변경에 영향받지 않는
 * 단조 증가 시간을 사용하며, 테스트에서는 시간을 직접 제어하는 함수로 대체할 수 있다.
 */
internal class ExpiringLruCache<K : Any, V : Any>(
    private val maxEntries: Int = 5,
    private val ttlMillis: Long = 5 * 60 * 1_000L,
    private val nowMillis: () -> Long = { System.nanoTime() / 1_000_000 },
) {
    private val entries = LinkedHashMap<K, Entry<V>>(16, 0.75f, true)

    init {
        require(maxEntries > 0)
        require(ttlMillis > 0)
    }

    @Synchronized
    fun get(key: K): V? {
        val entry = entries[key] ?: return null
        if (nowMillis() - entry.createdAtMillis >= ttlMillis) {
            entries.remove(key = key)
            return null
        }
        return entry.value
    }

    @Synchronized
    fun getOrPut(key: K, createValue: () -> V): V {
        get(key = key)?.let { return it }
        val value = createValue()
        val now = nowMillis()
        entries.entries.removeAll { now - it.value.createdAtMillis >= ttlMillis }
        entries[key] = Entry(value = value, createdAtMillis = now)
        if (entries.size > maxEntries) {
            val oldest = entries.entries.iterator()
            oldest.next()
            oldest.remove()
        }
        return value
    }

    private data class Entry<V>(val value: V, val createdAtMillis: Long)
}
