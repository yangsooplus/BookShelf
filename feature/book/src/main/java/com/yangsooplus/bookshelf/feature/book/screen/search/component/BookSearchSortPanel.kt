package com.yangsooplus.bookshelf.feature.book.screen.search.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
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
    onDismiss: () -> Unit,
    onApply: (BookSearchSort) -> Unit,
) {
    val windowSize = currentWindowAdaptiveInfoV2().windowSizeClass
    val useDialog = windowSize.isWidthAtLeastBreakpoint(widthDpBreakpoint = 600)
    var selectedSort by rememberSaveable(sort) { mutableStateOf(value = sort) }
    val options: @Composable () -> Unit = {
        BookSearchSort.entries.forEach { option ->
            BSSelectionOption(
                label = option.label,
                selected = selectedSort == option,
                onClick = { selectedSort = option },
            )
        }
        BSButton(label = "적용", onClick = { onApply(selectedSort) }, modifier = Modifier.fillMaxWidth())
    }

    if (useDialog) {
        BSDialog(title = "검색 결과 정렬", onDismissRequest = onDismiss, content = options)
    } else {
        BSBottomSheet(title = "검색 결과 정렬", onDismissRequest = onDismiss, content = options)
    }
}
