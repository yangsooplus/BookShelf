package com.yangsooplus.bookshelf.feature.book.screen.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yangsooplus.bookshelf.core.designsystem.component.BSBottomSheet
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSDialog
import com.yangsooplus.bookshelf.core.designsystem.component.BSSelectionOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookSearchSortPanel(
    sort: BookSearchSort,
    useDialog: Boolean,
    onDismiss: () -> Unit,
    onApply: (BookSearchSort) -> Unit,
) {
    var selectedSort by rememberSaveable(sort) { mutableStateOf(sort) }
    val options: @Composable () -> Unit = {
        BookSearchSort.entries.forEach { option ->
            BSSelectionOption(option.label, selectedSort == option, { selectedSort = option })
        }
        BSButton("적용", { onApply(selectedSort) }, Modifier.fillMaxWidth())
    }
    if (useDialog) {
        BSDialog("검색 결과 정렬", onDismiss, content = options)
    } else {
        BSBottomSheet("검색 결과 정렬", onDismiss, content = options)
    }
}
