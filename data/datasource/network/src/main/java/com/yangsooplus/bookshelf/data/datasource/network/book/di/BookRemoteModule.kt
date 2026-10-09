package com.yangsooplus.bookshelf.data.datasource.network.book.di

import com.yangsooplus.bookshelf.data.datasource.network.book.BookApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object BookRemoteModule {
    @Provides
    @Singleton
    fun provideBookApiService(retrofit: Retrofit): BookApiService =
        retrofit.create(BookApiService::class.java)
}
