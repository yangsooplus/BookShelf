package com.yangsooplus.bookshelf.feature.book.screen.search

import androidx.lifecycle.viewModelScope
import com.yangsooplus.bookshelf.core.mvi.BaseViewModel
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.usecase.GetBooksUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchPageStatus
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchSort
import com.yangsooplus.bookshelf.feature.book.screen.search.BookSearchState.BookSearchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookSearchViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val setFavoriteBookUseCase: SetFavoriteBookUseCase,
) : BaseViewModel<BookSearchIntent, BookSearchState, BookSearchEffect, BookSearchReducer>(
    BookSearchState()
) {
    override fun intent(intent: BookSearchIntent) {
        super.intent(intent = intent)
        viewModelScope.launch(context = internalExceptionHandler) {
            when (intent) {
                is BookSearchIntent.ChangeQuery -> emitReducer(
                    reducer = BookSearchReducer.UpdateQuery(query = intent.query),
                )

                is BookSearchIntent.Search -> withCurrentState { state ->
                    search(query = intent.query, sort = state.sort)
                }

                is BookSearchIntent.ChangeSort -> changeSort(sort = intent.sort)
                is BookSearchIntent.ClickBook -> withCurrentState { state ->
                    val book =
                        state.books.find { it.id == intent.book.id } ?: return@withCurrentState
                    emitEffect(effect = BookSearchEffect.Navigation.OpenDetail(book = book))
                }

                is BookSearchIntent.UpdateFavorite -> emitReducer(
                    reducer = BookSearchReducer.UpdateFavorite(book = intent.book),
                )
                is BookSearchIntent.ToggleFavorite -> toggleFavorite(book = intent.book)
                BookSearchIntent.OpenSortPanel -> emitReducer(reducer = BookSearchReducer.ShowSortPanel)
                BookSearchIntent.CloseSortPanel -> emitReducer(reducer = BookSearchReducer.HideSortPanel)
                BookSearchIntent.LoadMore -> loadMore()
                BookSearchIntent.Retry -> withCurrentState { state ->
                    if (state.searchedQuery.isNotBlank()) search(
                        query = state.searchedQuery,
                        sort = state.sort
                    )
                }
            }
        }
    }

    private suspend fun changeSort(sort: BookSearchSort) = withCurrentState { state ->
        emitReducer(reducer = BookSearchReducer.HideSortPanel)
        if (sort == state.sort) return@withCurrentState
        emitReducer(reducer = BookSearchReducer.UpdateSort(sort = sort))
        search(query = state.searchedQuery, sort = sort)
    }

    private suspend fun search(query: String, sort: BookSearchSort) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            emitReducer(reducer = BookSearchReducer.ResetSearchResults)
            return
        }
        emitReducer(reducer = BookSearchReducer.ClearBooks)
        emitReducer(reducer = BookSearchReducer.StartSearch(query = normalizedQuery, sort = sort))
        getBooks(query = normalizedQuery, sort = sort, page = 1)
    }

    private suspend fun loadMore() {
        withCurrentState { currentState ->
            if (currentState.status != BookSearchStatus.Results ||
                currentState.pageStatus !in setOf(
                    BookSearchPageStatus.MoreAvailable,
                    BookSearchPageStatus.Error
                )
            ) return@withCurrentState
            emitReducer(reducer = BookSearchReducer.ShowPageLoading)
            getBooks(
                query = currentState.searchedQuery,
                sort = currentState.sort,
                page = currentState.page + 1,
            )
        }
    }

    private suspend fun getBooks(query: String, sort: BookSearchSort, page: Int) {
        val result = getBooksUseCase(
            args = GetBooksUseCase.Param(
                query = query,
                sort = when (sort) {
                    BookSearchSort.Accuracy -> GetBooksUseCase.Param.Sort.ACCURACY
                    BookSearchSort.PublishedDate -> GetBooksUseCase.Param.Sort.LATEST
                },
                page = page,
            )
        )

        when (result) {
            is GetBooksUseCase.Result.Success -> {
                emitReducer(
                    reducer = BookSearchReducer.UpdateBooks(books = result.books, page = page),
                )
            }

            GetBooksUseCase.Result.NoSearchResults, GetBooksUseCase.Result.NoMoreBooks -> {
                if (page == 1) {
                    emitReducer(reducer = BookSearchReducer.ShowEmptyResults)
                } else {
                    emitReducer(reducer = BookSearchReducer.MarkEndOfResults)
                }
            }

            GetBooksUseCase.Result.InvalidQuery -> {
                emitReducer(reducer = BookSearchReducer.ResetSearchResults)
            }

            GetBooksUseCase.Result.InvalidPage -> {
                if (page > 1) {
                    emitReducer(reducer = BookSearchReducer.MarkEndOfResults)
                } else {
                    emitReducer(reducer = BookSearchReducer.ShowLoadError(isNextPage = false))
                }
            }

            is GetBooksUseCase.Result.Fail -> {
                emitReducer(reducer = BookSearchReducer.ShowLoadError(isNextPage = page > 1))
            }
        }
    }

    private suspend fun toggleFavorite(book: Book) {
        when (val result = setFavoriteBookUseCase(
            args = SetFavoriteBookUseCase.Param(
                book = book,
                isFavorite = !book.isFavorite,
            )
        )) {
            is SetFavoriteBookUseCase.Result.Success -> {
                emitReducer(reducer = BookSearchReducer.UpdateFavorite(book = result.book))
                emitEffect(effect = BookSearchEffect.UpdateFavorite(book = result.book))
            }

            is SetFavoriteBookUseCase.Result.Fail -> emitEffect(
                effect = BookSearchEffect.ShowMessage(message = "즐겨찾기를 변경하지 못했어요. 다시 시도해주세요."),
            )
        }
    }

    override suspend fun onHandleException(
        coroutineContext: CoroutineContext,
        throwable: Throwable
    ) {
        withCurrentState { currentState ->
            if (currentState.status == BookSearchStatus.Loading ||
                currentState.pageStatus == BookSearchPageStatus.Loading
            ) {
                emitReducer(reducer = BookSearchReducer.ShowLoadError(isNextPage = currentState.page > 0))
            }
        }
        emitEffect(effect = BookSearchEffect.ShowMessage(message = "요청을 처리하지 못했어요. 다시 시도해주세요."))
    }
}
