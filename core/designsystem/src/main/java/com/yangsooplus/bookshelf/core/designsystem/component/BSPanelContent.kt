package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

@Composable
internal fun BSPanelContent(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
    topPadding: Dp = Tokens.spacing20,
) {
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(
            start = Tokens.spacing20,
            end = Tokens.spacing20,
            top = topPadding,
            bottom = Tokens.spacing20,
        ),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing20),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing08)
        ) {
            BasicText(
                title,
                Modifier.weight(1f),
                style = BSTheme.typography.displaySmall.copy(color = BSTheme.colors.contentPrimary)
            )
            Box(
                modifier = Modifier.size(48.dp).clickable(
                    onClick = onDismissRequest,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ),
                contentAlignment = Alignment.Center,
            ) {
                BSIcon(BSIcon.Close, "닫기", Modifier.size(20.dp))
            }
        }
        Column(
            Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(Tokens.spacing20)
        ) { content() }
    }
}

@Preview(name = "Panel content", showBackground = true, widthDp = 390)
@Composable
private fun BSPanelContentPreview() = BSTheme {
    var selected by remember { mutableStateOf(0) }
    BSPanelContent("검색 결과 정렬", {}, content = {
        BSSelectionOption("정확도순", selected == 0, { selected = 0 })
        BSSelectionOption("발간일순", selected == 1, { selected = 1 })
        BSButton("적용", {}, Modifier.fillMaxWidth())
    })
}
