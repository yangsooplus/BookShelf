package com.yangsooplus.bookshelf.domain.book.exception

sealed class BookException : Exception() {
    class NoSearchResults : BookException()
    class NoMoreBooks : BookException()
}
