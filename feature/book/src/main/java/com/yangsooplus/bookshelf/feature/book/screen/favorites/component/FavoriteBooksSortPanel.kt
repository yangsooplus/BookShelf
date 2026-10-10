package com.yangsooplus.bookshelf.feature.book.screen.favorites.component

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
import androidx.compose.ui.tooling.preview.Preview
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.component.BSBottomSheet
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSDialog
import com.yangsooplus.bookshelf.core.designsystem.component.BSSelectionOption
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteBooksSortPanel(
    sort: FavoriteBooksSort,
    onDismiss: () -> Unit,
    onApply: (FavoriteBooksSort) -> Unit,
) {
    val windowSize = currentWindowAdaptiveInfoV2().windowSizeClass
    val useDialog = windowSize.isWidthAtLeastBreakpoint(widthDpBreakpoint = 600)
    var selectedSort by rememberSaveable(sort) { mutableStateOf(value = sort) }
    val options: @Composable () -> Unit = {
        FavoriteBooksSort.entries.forEach { option ->
            BSSelectionOption(
                label = option.label,
                selected = selectedSort == option,
                onClick = { selectedSort = option },
            )
        }
        BSButton(label = "적용", onClick = { onApply(selectedSort) }, modifier = Modifier.fillMaxWidth())
    }

    if (useDialog) {
        BSDialog(title = "즐겨찾기 정렬", onDismissRequest = onDismiss, content = options)
    } else {
        BSBottomSheet(title = "즐겨찾기 정렬", onDismissRequest = onDismiss, content = options)
    }
}

@Preview
@Composable
private fun FavoriteBooksSortPanelPreview() = BSTheme {
    FavoriteBooksSortPanel(sort = FavoriteBooksSort.TitleAscending, onDismiss = {}, onApply = {})
}
