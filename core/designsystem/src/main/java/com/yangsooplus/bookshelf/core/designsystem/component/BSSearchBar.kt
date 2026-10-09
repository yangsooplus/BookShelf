package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

@Composable
fun BSSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "제목 또는 저자를 검색하세요",
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(Tokens.radius12)
    var focused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(BSTheme.colors.backgroundInputNormal, shape)
            .then(
                if (focused) Modifier.border(
                    1.dp,
                    BSTheme.colors.borderPrimaryActivated,
                    shape
                ) else Modifier
            )
            .padding(horizontal = Tokens.spacing14),
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BSIcon(BSIcon.Search, null, Modifier.size(20.dp))

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = Tokens.spacing14)
                .onFocusChanged { focused = it.isFocused }
                .semantics { contentDescription = "도서 검색" },
            enabled = enabled,
            singleLine = true,
            textStyle = BSTheme.typography.bodyMedium.copy(color = if (enabled) BSTheme.colors.contentPrimary else BSTheme.colors.contentDisabled),
            cursorBrush = SolidColor(BSTheme.colors.contentInteractive),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            decorationBox = { input ->
                Box {
                    if (query.isEmpty()) {
                        BasicText(
                            text = placeholder,
                            style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.contentPlaceholder)
                        )
                    }
                    input()
                }
            },
        )

        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        onClick = { onQueryChange("") },
                        enabled = enabled,
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                BSIcon(BSIcon.Close, "검색어 지우기", Modifier.size(20.dp))
            }
        }
    }
}

@Preview(name = "Search · default", showBackground = true, widthDp = 350)
@Composable
private fun BSSearchBarDefaultPreview() = SearchBarPreview("")

@Preview(name = "Search · filled", showBackground = true, widthDp = 350)
@Composable
private fun BSSearchBarFilledPreview() = SearchBarPreview("기록")

@Preview(name = "Search · focused", showBackground = true, widthDp = 350)
@Composable
private fun BSSearchBarFocusedPreview() = SearchBarPreview("기록", requestFocus = true)

@Composable
private fun SearchBarPreview(initialQuery: String, requestFocus: Boolean = false) = BSTheme {
    var query by remember { mutableStateOf(initialQuery) }
    val focusRequester = remember { FocusRequester() }
    BSSearchBar(query, { query = it }, {}, Modifier.focusRequester(focusRequester))
    LaunchedEffect(requestFocus) {
        if (requestFocus) focusRequester.requestFocus()
    }
}
