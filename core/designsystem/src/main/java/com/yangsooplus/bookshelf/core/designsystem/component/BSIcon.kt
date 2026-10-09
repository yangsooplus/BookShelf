package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.R
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

enum class BSIcon(
    @param:DrawableRes internal val resource: Int,
) {
    Search(R.drawable.bsds_search),
    Heart(R.drawable.bsds_heart),
    HeartFilled(R.drawable.bsds_heart_filled),
    Back(R.drawable.bsds_back),
    Close(R.drawable.bsds_close),
    Sort(R.drawable.bsds_sort),
    Filter(R.drawable.bsds_filter),
    Book(R.drawable.bsds_book),
    Image(R.drawable.bsds_image),
    Check(R.drawable.bsds_check),
    Offline(R.drawable.bsds_offline),
    More(R.drawable.bsds_more),
}

@Composable
fun BSIcon(
    icon: BSIcon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = BSTheme.colors.contentPrimary,
) {
    Image(
        painter = painterResource(icon.resource),
        contentDescription = contentDescription,
        modifier = modifier.size(24.dp),
        colorFilter = ColorFilter.tint(tint),
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
internal fun BSIconsPreview() = BSTheme {
    Column(
        Modifier.background(BSTheme.colors.backgroundPrimary).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BSIcon.entries.forEach { icon ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BSIcon(icon, null)
                BasicText(
                    icon.name,
                    style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.contentPrimary)
                )
            }
        }
    }
}
