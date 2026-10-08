package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

enum class BSButtonStyle { Primary, Subtle, Secondary }

@Composable
fun BSButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: BSButtonStyle = BSButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(Tokens.radius08)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val container = when {
        !enabled -> BSTheme.colors.buttonDisabled
        style == BSButtonStyle.Primary && pressed -> BSTheme.colors.buttonPrimaryHover
        style == BSButtonStyle.Primary -> BSTheme.colors.buttonPrimary
        style == BSButtonStyle.Subtle -> BSTheme.colors.buttonDefault
        else -> BSTheme.colors.buttonSecondary
    }
    val content = when {
        !enabled -> BSTheme.colors.contentDisabled
        style == BSButtonStyle.Primary -> BSTheme.colors.contentInversePrimary
        else -> BSTheme.colors.contentPrimary
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(container)
            .then(
                if (style == BSButtonStyle.Subtle) {
                    Modifier.border(
                        1.dp,
                        BSTheme.colors.buttonDefaultOutline,
                        shape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(Tokens.spacing12),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = BSTheme.typography.ui14.copy(color = content)
        )
    }
}

@Preview(name = "Button · styles and states", showBackground = true, widthDp = 350)
@Composable
private fun BSButtonPreview() = BSTheme {
    Column(verticalArrangement = Arrangement.spacedBy(Tokens.spacing08)) {
        BSButtonStyle.entries.forEach { style ->
            BSButton(style.name, {}, Modifier.fillMaxWidth(), style = style)
            BSButton(
                "${style.name} · Disabled",
                {},
                Modifier.fillMaxWidth(),
                style = style,
                enabled = false
            )
        }
    }
}
