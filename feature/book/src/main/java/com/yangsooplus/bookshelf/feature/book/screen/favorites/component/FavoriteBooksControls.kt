package com.yangsooplus.bookshelf.feature.book.screen.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSButtonStyle
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.screen.favorites.FavoriteBooksState.FavoriteBooksSort

@Composable
internal fun FavoriteBooksControls(
    totalCount: Int,
    sort: FavoriteBooksSort,
    isPriceFilterApplied: Boolean,
    onOpenPricePanel: () -> Unit,
    onOpenSortPanel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = "저장한 책 ${totalCount}권",
            modifier = Modifier.weight(weight = 1f),
            style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.supportNormal),
        )
        BSButton(
            label = if (isPriceFilterApplied) "필터 적용" else "가격 필터",
            onClick = onOpenPricePanel,
            modifier = Modifier.width(width = 88.dp),
            style = BSButtonStyle.Subtle,
        )
        BSButton(label = sort.label, onClick = onOpenSortPanel, modifier = Modifier.width(width = 118.dp), style = BSButtonStyle.Subtle)
    }
}

@Preview(showBackground = true, widthDp = 350)
@Composable
private fun FavoriteBooksControlsPreview() = BSTheme {
    FavoriteBooksControls(totalCount = 4, sort = FavoriteBooksSort.TitleAscending, isPriceFilterApplied = false, onOpenPricePanel = {}, onOpenSortPanel = {})
}
