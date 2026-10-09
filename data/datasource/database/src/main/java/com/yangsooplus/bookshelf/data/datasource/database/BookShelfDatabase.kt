package com.yangsooplus.bookshelf.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookDao
import com.yangsooplus.bookshelf.data.datasource.database.book.FavoriteBookEntity

@Database(entities = [FavoriteBookEntity::class], version = 1, exportSchema = true)
@TypeConverters(BookTypeConverters::class)
internal abstract class BookShelfDatabase : RoomDatabase() {
    abstract fun favoriteBookDao(): FavoriteBookDao
}
