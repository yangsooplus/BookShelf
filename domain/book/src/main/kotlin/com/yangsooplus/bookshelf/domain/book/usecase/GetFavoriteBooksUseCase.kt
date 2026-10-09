package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject

class GetFavoriteBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<GetFavoriteBooksUseCase.Param, GetFavoriteBooksUseCase.Result> {
    override suspend fun invoke(args: Param): Result {
        return runCatchingCancellable<Result>(
            onFailure = { e ->
                when (e) {
                    is BookException.NoSearchResults -> Result.NoSearchResults
                    is BookException.NoMoreBooks -> Result.NoMoreBooks
                    is BookException.InvalidPage -> Result.InvalidPage
                    else -> Result.Fail(e)
                }
            },
        ) {
            val books = bookRepository.getFavoriteBooks(
                query = args.query.trim(),
                sort = args.sort.name.lowercase(),
                minPrice = args.minPrice,
                maxPrice = args.maxPrice,
                page = args.page,
                size = PAGE_SIZE,
            )

            if (books.isEmpty()) Result.NoSearchResults else Result.Success(books = books)
        }
    }

    data class Param(
        val sort: Sort,
        val minPrice: Int? = null,
        val maxPrice: Int? = null,
        val page: Int = 1,
        val query: String = "",
    ) {
        enum class Sort {
            ASC, DESC
        }
    }

    sealed interface Result {
        data class Success(val books: List<Book>) : Result
        data object NoSearchResults : Result
        data object NoMoreBooks : Result
        data object InvalidPage : Result
        data class Fail(val cause: Exception) : Result
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}
