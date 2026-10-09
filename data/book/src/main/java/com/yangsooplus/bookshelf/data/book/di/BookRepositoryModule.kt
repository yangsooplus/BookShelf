package com.yangsooplus.bookshelf.data.book.di

import com.yangsooplus.bookshelf.data.book.repository.DefaultBookRepository
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BookRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBookRepository(repository: DefaultBookRepository): BookRepository
}
