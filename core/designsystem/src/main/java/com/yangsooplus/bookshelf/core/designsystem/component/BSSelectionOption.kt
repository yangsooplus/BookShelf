package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

@Composable
fun BSSelectionOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().height(48.dp)
            .background(
                if (selected) BSTheme.colors.supportPositiveAlt else BSTheme.colors.backgroundPrimary,
                RoundedCornerShape(Tokens.radius08)
            )
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .padding(Tokens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing12),
    ) {
        BasicText(
            label,
            Modifier.weight(1f),
            style = BSTheme.typography.bodyMedium.copy(color = if (selected) BSTheme.colors.contentInteractive else BSTheme.colors.contentPrimary)
        )
        if (selected) BSIcon(BSIcon.Check, null, Modifier.size(20.dp))
    }
}

@Preview(name = "Selection option · selected / unselected", showBackground = true, widthDp = 350)
@Composable
private fun BSSelectionOptionPreview() = BSTheme {
    var selected by remember { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(Tokens.spacing12)) {
        BSSelectionOption("정확도순", selected == 0, { selected = 0 })
        BSSelectionOption("발간일순", selected == 1, { selected = 1 })
    }
}
