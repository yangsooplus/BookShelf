package com.yangsooplus.bookshelf.feature.book.navigation

import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.time.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object BookSerializer : KSerializer<Book> {
    override val descriptor = SavedBook.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Book) {
        encoder.encodeSerializableValue(
            SavedBook.serializer(),
            SavedBook(
                id = value.id,
                title = value.title,
                contents = value.contents,
                url = value.url,
                authors = value.authors,
                publisher = value.publisher,
                publishedDate = value.publishedDate.toEpochDay(),
                regularPrice = value.price.regularPrice,
                salePrice = value.price.salePrice,
                thumbnailUrl = value.thumbnailUrl,
                isbn = value.isbn,
                translators = value.translators,
                isFavorite = value.isFavorite,
            ),
        )
    }

    override fun deserialize(decoder: Decoder): Book {
        val saved = decoder.decodeSerializableValue(SavedBook.serializer())
        return Book(
            id = saved.id,
            title = saved.title,
            contents = saved.contents,
            url = saved.url,
            authors = saved.authors,
            publisher = saved.publisher,
            publishedDate = LocalDate.ofEpochDay(saved.publishedDate),
            price = BookPrice(regularPrice = saved.regularPrice, salePrice = saved.salePrice),
            thumbnailUrl = saved.thumbnailUrl,
            isbn = saved.isbn,
            translators = saved.translators,
            isFavorite = saved.isFavorite,
        )
    }
}

@Serializable
private data class SavedBook(
    val id: String,
    val title: String,
    val contents: String,
    val url: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: Long,
    val regularPrice: Int,
    val salePrice: Int?,
    val thumbnailUrl: String,
    val isbn: String,
    val translators: List<String>,
    val isFavorite: Boolean,
)
