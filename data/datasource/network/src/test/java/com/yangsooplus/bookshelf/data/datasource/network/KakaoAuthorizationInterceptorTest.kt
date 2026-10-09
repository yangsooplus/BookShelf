package com.yangsooplus.bookshelf.data.datasource.network

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

class KakaoAuthorizationInterceptorTest {
    @Test
    fun `REST API 키를 KakaoAK 인증 헤더에 넣는다`() {
        MockWebServer().use { server ->
            server.start()
            server.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient.Builder()
                .addInterceptor(KakaoAuthorizationInterceptor("test-key"))
                .build()
            client.newCall(Request.Builder().url(server.url("/v3/search/book")).build())
                .execute().use { assertEquals(200, it.code) }
            assertEquals("KakaoAK test-key", server.takeRequest(1, TimeUnit.SECONDS)?.getHeader("Authorization"))
        }
    }

    @Test
    fun `키가 없으면 요청을 보내기 전에 설정 오류를 전달한다`() {
        MockWebServer().use { server ->
            server.start()
            val client = OkHttpClient.Builder()
                .addInterceptor(KakaoAuthorizationInterceptor(""))
                .build()
            try {
                client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()
                fail("IOException expected")
            } catch (exception: IOException) {
                assertTrue(exception.message.orEmpty().contains("KAKAO_REST_API_KEY"))
            }
            assertEquals(0, server.requestCount)
        }
    }
}
