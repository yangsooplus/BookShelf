package com.yangsooplus.bookshelf.domain.book.usecase

interface UseCase<A, T> {
    suspend operator fun invoke(args: A): T
}
