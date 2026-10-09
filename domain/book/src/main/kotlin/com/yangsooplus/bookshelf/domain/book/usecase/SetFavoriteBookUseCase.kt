package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class SetFavoriteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<SetFavoriteBookUseCase.Param, SetFavoriteBookUseCase.Result> {
    override suspend fun invoke(args: Param): Result = try {
        bookRepository.setFavorite(args.book, args.isFavorite)
        Result.Success
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Fail(e)
    }

    data class Param(val book: Book, val isFavorite: Boolean)

    sealed interface Result {
        data object Success : Result
        data class Fail(val cause: Exception) : Result
    }
}
