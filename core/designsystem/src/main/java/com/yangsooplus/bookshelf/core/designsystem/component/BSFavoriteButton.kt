package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

@Composable
fun BSFavoriteButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(BSTheme.colors.backgroundPrimary)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ),
        contentAlignment = Alignment.Center,
    ) {
        BSIcon(
            if (checked) BSIcon.HeartFilled else BSIcon.Heart,
            modifier = Modifier.size(20.dp),
            contentDescription = if (checked) "즐겨찾기 해제" else "즐겨찾기 추가",
            tint = if (!enabled) BSTheme.colors.contentDisabled else if (checked) BSTheme.colors.supportFavorite else BSTheme.colors.supportFavoriteAlt,
        )
    }
}

internal class BSFavoriteButtonPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(name = "Favorite")
@Composable
private fun BSFavoriteButtonPreview(
    @PreviewParameter(BSFavoriteButtonPreviewProvider::class) checked: Boolean,
) = BSTheme {
    var value by remember(checked) { mutableStateOf(checked) }
    BSFavoriteButton(value, { value = it })
}
