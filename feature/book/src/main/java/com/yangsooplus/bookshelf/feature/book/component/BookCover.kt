package com.yangsooplus.bookshelf.feature.book.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

@Composable
internal fun BookCover(
    thumbnailUrl: String,
    modifier: Modifier = Modifier.size(width = 80.dp, height = 112.dp),
) {
    var imageLoaded by remember(key1 = thumbnailUrl) { mutableStateOf(value = false) }
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = BSTheme.colors.backgroundInputNormal),
        contentAlignment = Alignment.Center,
    ) {
        if (!imageLoaded) {
            BSIcon(
                icon = BSIcon.Image,
                contentDescription = "표지 이미지 없음",
                modifier = Modifier.size(size = 32.dp),
                tint = BSTheme.colors.contentSecondary
            )
        }
        if (thumbnailUrl.isNotBlank()) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "도서 표지",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { imageLoaded = true },
                onLoading = { imageLoaded = false },
                onError = { imageLoaded = false },
            )
        }
    }
}

@Preview
@Composable
private fun BookCoverPreview() = BSTheme {
    BookCover(thumbnailUrl = "")
}
