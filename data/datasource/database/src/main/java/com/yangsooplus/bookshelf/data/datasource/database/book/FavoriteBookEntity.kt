package com.yangsooplus.bookshelf.data.datasource.database.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "favorite_books", indices = [Index(value = ["title", "id"])])
data class FavoriteBookEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val title: String,
    val contents: String,
    val url: String,
    val authors: List<String>,
    val publisher: String,
    @ColumnInfo(name = "published_date") val publishedDate: LocalDate,
    @ColumnInfo(name = "regular_price") val regularPrice: Int,
    @ColumnInfo(name = "sale_price") val salePrice: Int?,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    val isbn: String,
    val translators: List<String>,
)
