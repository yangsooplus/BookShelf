package com.yangsooplus.bookshelf.data.datasource.database

import androidx.room.TypeConverter
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class BookTypeConverters {
    @TypeConverter
    fun encodeNames(names: List<String>): String = Json.encodeToString(names)

    @TypeConverter
    fun decodeNames(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun encodeDate(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun decodeDate(value: Long): LocalDate = LocalDate.ofEpochDay(value)
}
