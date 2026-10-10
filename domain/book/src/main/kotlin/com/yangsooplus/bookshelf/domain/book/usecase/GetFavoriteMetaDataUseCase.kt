package com.yangsooplus.bookshelf.domain.book.usecase

import com.yangsooplus.bookshelf.domain.book.repository.BookRepository
import javax.inject.Inject

class GetFavoriteMetaDataUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) : UseCase<Unit, GetFavoriteMetaDataUseCase.Result> {
    override suspend fun invoke(args: Unit): Result = runCatchingCancellable<Result>(
        onFailure = { Result.Fail(it) },
    ) {
        val metaData = bookRepository.getFavoriteMetaData()
        Result.Success(totalCount = metaData.totalCount, maxPrice = metaData.maxPrice)
    }

    sealed interface Result {
        data class Success(val totalCount: Int, val maxPrice: Int?) : Result
        data class Fail(val cause: Exception) : Result
    }
}
