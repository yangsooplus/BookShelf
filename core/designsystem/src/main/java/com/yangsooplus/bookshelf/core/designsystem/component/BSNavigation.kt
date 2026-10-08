package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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

data class BSNavigationItem(val label: String, val icon: BSIcon)

@Composable
fun BSBottomNavigation(
    items: List<BSNavigationItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Tokens.radius08)
    Row(
        modifier = modifier.fillMaxWidth().height(76.dp)
            .background(BSTheme.colors.backgroundPrimary, shape)
            .border(1.dp, BSTheme.colors.borderPrimary, shape)
            .padding(Tokens.spacing08).selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            NavigationItem(
                item,
                selectedIndex == index,
                { onSelect(index) },
                Modifier.weight(1f).height(48.dp)
            )
        }
    }
}

@Composable
fun BSNavigationRail(
    items: List<BSNavigationItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(80.dp).fillMaxHeight()
            .background(BSTheme.colors.backgroundPrimary)
            .padding(
                start = Tokens.spacing08,
                end = Tokens.spacing08,
                top = Tokens.spacing24,
                bottom = Tokens.spacing08
            )
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing12),
    ) {
        items.forEachIndexed { index, item ->
            NavigationItem(item, selectedIndex == index, { onSelect(index) }, Modifier.size(64.dp))
        }
    }
}

@Composable
private fun NavigationItem(
    item: BSNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing04, Alignment.CenterVertically),
    ) {
        BSIcon(item.icon, null, Modifier.size(20.dp))
        BasicText(
            item.label,
            style = BSTheme.typography.captionLarge.copy(color = if (selected) BSTheme.colors.contentInteractive else BSTheme.colors.supportNormal)
        )
        Box(
            Modifier.width(40.dp).height(3.dp)
                .background(if (selected) BSTheme.colors.contentInteractive else BSTheme.colors.backgroundPrimary)
        )
    }
}

private val previewNavigationItems = listOf(
    BSNavigationItem("검색", BSIcon.Search),
    BSNavigationItem("즐겨찾기", BSIcon.Heart),
)

@Preview(name = "Bottom navigation · selection", showBackground = true, widthDp = 390)
@Composable
private fun BSBottomNavigationPreview() = BSTheme {
    var selected by remember { mutableStateOf(0) }
    BSBottomNavigation(previewNavigationItems, selected, { selected = it })
}

@Preview(name = "Rail · selection", showBackground = true, widthDp = 80, heightDp = 360)
@Composable
private fun BSNavigationRailPreview() = BSTheme {
    var selected by remember { mutableStateOf(1) }
    BSNavigationRail(previewNavigationItems, selected, { selected = it })
}
