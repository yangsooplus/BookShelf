package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.exception.BookException
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class GetFavoriteBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<GetFavoriteBooksUseCase.Param, GetFavoriteBooksUseCase.Result> {
    override suspend fun invoke(args: Param): Result {
        if (args.page < 1) return Result.InvalidPage

        return try {
            val books = bookRepository.getFavoriteBooks(
                query = args.query.trim(),
                sort = args.sort.name.lowercase(),
                minPrice = args.minPrice,
                maxPrice = args.maxPrice,
                page = args.page,
                size = PAGE_SIZE,
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
