package com.yangsooplus.bookshelf.data.datasource.network

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

internal class KakaoAuthorizationInterceptor(
    private val restApiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (restApiKey.isBlank()) {
            throw IOException("local.properties에 KAKAO_REST_API_KEY를 설정해주세요.")
        }
        val request = chain.request().newBuilder()
            .header("Authorization", "KakaoAK $restApiKey")
            .build()
        return chain.proceed(request)
    }
}
