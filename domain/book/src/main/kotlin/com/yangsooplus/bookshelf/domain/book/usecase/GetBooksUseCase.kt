package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<GetBooksUseCase.Param, GetBooksUseCase.Result> {
    override suspend fun invoke(args: Param): Result {
        val query = args.query.trim()
        if (query.isEmpty()) return Result.InvalidQuery
        if (args.page !in 1..MAX_PAGE) return Result.InvalidPage

        return try {
            val books = bookRepository.getBooks(
                query = query,
                sort = args.sort.name.lowercase(),
                page = args.page,
                size = PAGE_SIZE
            )

            Result.Success(books = books)
        } catch (e: BookException.NoSearchResults) {
            Result.NoSearchResults
        } catch (e: BookException.NoMoreBooks) {
            Result.NoMoreBooks
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Fail(e)
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
        const val MAX_PAGE = 50
    }
}
