package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject

class SetFavoriteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<SetFavoriteBookUseCase.Param, SetFavoriteBookUseCase.Result> {
    override suspend fun invoke(args: Param): Result = runCatchingCancellable<Result>(
        onFailure = { Result.Fail(it) },
    ) {
        bookRepository.setFavorite(book = args.book, isFavorite = args.isFavorite)
        Result.Success
    }

    data class Param(val book: Book, val isFavorite: Boolean)

    sealed interface Result {
        data object Success : Result
        data class Fail(val cause: Exception) : Result
    }
}
