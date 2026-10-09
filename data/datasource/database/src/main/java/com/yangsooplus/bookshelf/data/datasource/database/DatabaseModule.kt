package com.yangsooplus.bookshelf.data.datasource.database

import android.content.Context
import androidx.room.Room
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BookShelfDatabase =
        Room.databaseBuilder(context, BookShelfDatabase::class.java, "bookshelf.db").build()

    @Provides
    @Singleton
    fun provideFavoriteBookDao(database: BookShelfDatabase): FavoriteBookDao =
        database.favoriteBookDao()
}
