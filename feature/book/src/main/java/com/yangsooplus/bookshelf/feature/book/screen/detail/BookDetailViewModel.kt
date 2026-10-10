package com.yangsooplus.bookshelf.feature.book.screen.detail

import androidx.lifecycle.viewModelScope
import com.yangsooplus.bookshelf.core.mvi.BaseViewModel
import com.yangsooplus.bookshelf.domain.book.usecase.SetFavoriteBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookDetailViewModel @Inject constructor(
    private val setFavoriteBookUseCase: SetFavoriteBookUseCase,
) : BaseViewModel<BookDetailIntent, BookDetailState, BookDetailEffect, BookDetailReducer>(
    initialState = BookDetailState(),
) {
    override fun intent(intent: BookDetailIntent) {
        super.intent(intent = intent)
        viewModelScope.launch(context = internalExceptionHandler) {
            when (intent) {
                is BookDetailIntent.Init -> withCurrentState { state ->
                    if (state.book?.id != intent.book.id) {
                        emitReducer(reducer = BookDetailReducer.UpdateBook(book = intent.book))
                    }
                }
                BookDetailIntent.GoBack -> emitEffect(effect = BookDetailEffect.GoBack)
                BookDetailIntent.ToggleFavorite -> toggleFavorite()
                is BookDetailIntent.UpdateFavorite -> withCurrentState { state ->
                    val book = state.book ?: return@withCurrentState
                    if (book.id == intent.book.id) {
                        emitReducer(
                            reducer = BookDetailReducer.UpdateBook(
                                book = book.copy(isFavorite = intent.book.isFavorite),
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun toggleFavorite() = withCurrentState { state ->
        val book = state.book ?: return@withCurrentState
        when (val result = setFavoriteBookUseCase(
            args = SetFavoriteBookUseCase.Param(
                book = book,
                isFavorite = !book.isFavorite,
            )
        )) {
            is SetFavoriteBookUseCase.Result.Success -> {
                emitReducer(reducer = BookDetailReducer.UpdateBook(book = result.book))
                emitEffect(effect = BookDetailEffect.UpdateFavorite(book = result.book))
            }

            is SetFavoriteBookUseCase.Result.Fail -> emitEffect(
                effect = BookDetailEffect.ShowMessage(message = "즐겨찾기를 변경하지 못했어요. 다시 시도해주세요."),
            )
        }
    }

    override suspend fun onHandleException(
        coroutineContext: CoroutineContext,
        throwable: Throwable
    ) {
        emitEffect(effect = BookDetailEffect.ShowMessage(message = "요청을 처리하지 못했어요. 다시 시도해주세요."))
    }

}
