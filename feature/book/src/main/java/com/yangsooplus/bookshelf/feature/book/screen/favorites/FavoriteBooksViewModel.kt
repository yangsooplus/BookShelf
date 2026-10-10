package com.yangsooplus.bookshelf.feature.book.screen.favorites

import androidx.lifecycle.viewModelScope
import com.yangsooplus.bookshelf.core.mvi.BaseViewModel
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.usecase.GetFavoriteBooksUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.GetFavoriteMetaDataUseCase
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksStatus
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksPageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteBooksViewModel @Inject constructor(
    private val getFavoriteBooksUseCase: GetFavoriteBooksUseCase,
    private val getFavoriteMetaDataUseCase: GetFavoriteMetaDataUseCase,
    private val setFavoriteBookUseCase: SetFavoriteBookUseCase,
) : BaseViewModel<FavoriteBooksIntent, FavoriteBooksState, FavoriteBooksEffect, FavoriteBooksReducer>(
    initialState = FavoriteBooksState(),
) {
    private var booksJob: Job? = null

    override fun intent(intent: FavoriteBooksIntent) {
        super.intent(intent = intent)
        viewModelScope.launch(context = internalExceptionHandler) {
            when (intent) {
                is FavoriteBooksIntent.ChangeQuery -> emitReducer(reducer = FavoriteBooksReducer.UpdateQuery(query = intent.query))
                is FavoriteBooksIntent.Search -> search(query = intent.query)
                is FavoriteBooksIntent.ChangeSort -> changeSort(sort = intent.sort)
                is FavoriteBooksIntent.ApplyPriceFilter -> {
                    emitReducer(reducer = FavoriteBooksReducer.HidePricePanel)
                    emitReducer(reducer = FavoriteBooksReducer.UpdatePriceFilter(minPrice = intent.minPrice, maxPrice = intent.maxPrice))
                    refreshBooks()
                }
                FavoriteBooksIntent.ResetPriceFilter -> {
                    emitReducer(reducer = FavoriteBooksReducer.HidePricePanel)
                    emitReducer(reducer = FavoriteBooksReducer.UpdatePriceFilter(minPrice = null, maxPrice = null))
                    refreshBooks()
                }
                is FavoriteBooksIntent.ClickBook -> withCurrentState { state ->
                    val book = state.books.find { it.id == intent.book.id } ?: return@withCurrentState
                    emitEffect(effect = FavoriteBooksEffect.Navigation.OpenDetail(book = book))
                }
                is FavoriteBooksIntent.RemoveFavorite -> removeFavorite(book = intent.book)
                FavoriteBooksIntent.OpenSortPanel -> emitReducer(reducer = FavoriteBooksReducer.ShowSortPanel)
                FavoriteBooksIntent.CloseSortPanel -> emitReducer(reducer = FavoriteBooksReducer.HideSortPanel)
                FavoriteBooksIntent.OpenPricePanel -> emitReducer(reducer = FavoriteBooksReducer.ShowPricePanel)
                FavoriteBooksIntent.ClosePricePanel -> emitReducer(reducer = FavoriteBooksReducer.HidePricePanel)
                FavoriteBooksIntent.LoadMore -> loadMore()
                FavoriteBooksIntent.EnterScreen, FavoriteBooksIntent.Retry -> {
                    getMetaData()
                    refreshBooks()
                }
                FavoriteBooksIntent.OpenSearch -> emitEffect(effect = FavoriteBooksEffect.Navigation.OpenSearch)
            }
        }
    }

    private suspend fun changeSort(sort: FavoriteBooksSort) = withCurrentState { state ->
        emitReducer(reducer = FavoriteBooksReducer.HideSortPanel)
        if (sort == state.sort) return@withCurrentState
        emitReducer(reducer = FavoriteBooksReducer.UpdateSort(sort = sort))
        refreshBooks()
    }

    private suspend fun refreshBooks() = withCurrentState { state -> search(query = state.searchedQuery) }

    private suspend fun search(query: String) {
        emitReducer(reducer = FavoriteBooksReducer.StartSearch(query = query.trim()))
        getBooks(page = 1)
    }

    private suspend fun loadMore() = withCurrentState { state ->
        if (state.status != FavoriteBooksStatus.Results ||
            state.pageStatus !in setOf(FavoriteBooksPageStatus.MoreAvailable, FavoriteBooksPageStatus.Error)
        ) return@withCurrentState
        emitReducer(reducer = FavoriteBooksReducer.ShowPageLoading)
        getBooks(page = state.page + 1)
    }

    private fun getBooks(page: Int) {
        booksJob?.cancel()
        booksJob = viewModelScope.launch(context = internalExceptionHandler) {
            withCurrentState { state ->
                val result = getFavoriteBooksUseCase(args = GetFavoriteBooksUseCase.Param(
                    query = state.searchedQuery,
                    sort = when (state.sort) {
                        FavoriteBooksSort.TitleAscending -> GetFavoriteBooksUseCase.Param.Sort.ASC
                        FavoriteBooksSort.TitleDescending -> GetFavoriteBooksUseCase.Param.Sort.DESC
                    },
                    minPrice = state.minPriceFilter,
                    maxPrice = state.maxPriceFilter,
                    page = page,
                ))
                coroutineContext.ensureActive()
                when (result) {
                    is GetFavoriteBooksUseCase.Result.Success -> emitReducer(
                        reducer = FavoriteBooksReducer.UpdateBooks(books = result.books, page = page),
                    )
                    GetFavoriteBooksUseCase.Result.NoSearchResults, GetFavoriteBooksUseCase.Result.NoMoreBooks -> {
                        if (page == 1) emitReducer(reducer = FavoriteBooksReducer.ShowEmptyResults)
                        else emitReducer(reducer = FavoriteBooksReducer.MarkEndOfResults)
                    }
                    GetFavoriteBooksUseCase.Result.InvalidPage -> emitReducer(reducer = FavoriteBooksReducer.MarkEndOfResults)
                    is GetFavoriteBooksUseCase.Result.Fail -> emitReducer(reducer = FavoriteBooksReducer.ShowLoadError(isNextPage = page > 1))
                }
            }
        }
    }

    private suspend fun getMetaData() {
        when (val result = getFavoriteMetaDataUseCase(args = Unit)) {
            is GetFavoriteMetaDataUseCase.Result.Success -> emitReducer(
                reducer = FavoriteBooksReducer.UpdateMetaData(totalCount = result.totalCount, maxPrice = result.maxPrice),
            )
            is GetFavoriteMetaDataUseCase.Result.Fail -> emitEffect(
                effect = FavoriteBooksEffect.ShowMessage(message = "즐겨찾기 정보를 불러오지 못했어요. 다시 시도해주세요."),
            )
        }
    }

    private suspend fun removeFavorite(book: Book) {
        when (val result = setFavoriteBookUseCase(args = SetFavoriteBookUseCase.Param(book = book, isFavorite = false))) {
            is SetFavoriteBookUseCase.Result.Success -> {
                emitReducer(reducer = FavoriteBooksReducer.RemoveBook(book = result.book))
                getMetaData()
                refreshBooks()
                emitEffect(effect = FavoriteBooksEffect.UpdateFavorite(book = result.book))
            }
            is SetFavoriteBookUseCase.Result.Fail -> emitEffect(
                effect = FavoriteBooksEffect.ShowMessage(message = "즐겨찾기를 해제하지 못했어요. 다시 시도해주세요."),
            )
        }
    }

    override suspend fun onHandleException(coroutineContext: CoroutineContext, throwable: Throwable) {
        withCurrentState { state ->
            if (state.status == FavoriteBooksStatus.Loading || state.pageStatus == FavoriteBooksPageStatus.Loading) {
                emitReducer(reducer = FavoriteBooksReducer.ShowLoadError(isNextPage = state.page > 0))
            }
        }
        emitEffect(effect = FavoriteBooksEffect.ShowMessage(message = "요청을 처리하지 못했어요. 다시 시도해주세요."))
    }
}
