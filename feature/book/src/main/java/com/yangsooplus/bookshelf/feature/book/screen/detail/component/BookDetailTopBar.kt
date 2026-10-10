package com.yangsooplus.bookshelf.feature.book.screen.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSFavoriteButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

@Composable
internal fun BookDetailTopBar(
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .background(color = BSTheme.colors.backgroundPrimary)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(size = 48.dp)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            BSIcon(
                icon = BSIcon.Back,
                contentDescription = "뒤로",
                modifier = Modifier.size(size = 20.dp)
            )
        }
        BasicText(
            text = "도서 상세",
            modifier = Modifier.weight(weight = 1f),
            style = BSTheme.typography.displaySmall.copy(color = BSTheme.colors.contentPrimary),
        )
        BSFavoriteButton(checked = isFavorite, onCheckedChange = { onFavoriteChange() })
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun BookDetailTopBarPreview() = BSTheme {
    BookDetailTopBar(isFavorite = true, onBack = {}, onFavoriteChange = {})
}
