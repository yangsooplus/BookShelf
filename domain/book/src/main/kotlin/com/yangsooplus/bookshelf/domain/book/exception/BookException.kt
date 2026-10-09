package com.yangsooplus.bookshelf.domain.book.exception

sealed class BookException(message: String? = null) : Exception(message) {
    class NoSearchResults : BookException()
    class NoMoreBooks : BookException()
    class InvalidArgument(message: String) : BookException(message = message)
    class InvalidPage(message: String) : BookException(message = message)
}
