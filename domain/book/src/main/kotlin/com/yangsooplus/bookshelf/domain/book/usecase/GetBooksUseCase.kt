package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<GetBooksUseCase.Param, GetBooksUseCase.Result> {
    override suspend fun invoke(args: Param): Result {
        val query = args.query.trim()
        if (query.isEmpty()) return Result.InvalidQuery

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
            val books = bookRepository.getBooks(
                query = query,
                sort = args.sort.name.lowercase(),
                page = args.page,
                size = PAGE_SIZE
            )

            if (books.isEmpty()) Result.NoSearchResults else Result.Success(books = books)
        }
    }

    data class Param(
        val query: String,
        val sort: Sort,
        val page: Int,
    ) {
        enum class Sort {
            ACCURACY, LATEST
        }
    }


    sealed interface Result {
        data class Success(val books: List<Book>) : Result
        data object NoSearchResults : Result
        data object NoMoreBooks : Result
        data object InvalidQuery : Result
        data object InvalidPage : Result
        data class Fail(val cause: Exception) : Result
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
