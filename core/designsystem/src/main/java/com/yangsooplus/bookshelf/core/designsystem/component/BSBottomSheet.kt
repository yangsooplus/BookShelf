package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import kotlinx.coroutines.launch
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

@ExperimentalMaterial3Api
@Composable
fun BSBottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {},
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
            shouldDismissOnClickOutside = false,
        ),
        shape = RoundedCornerShape(topStart = Tokens.radius20, topEnd = Tokens.radius20),
        containerColor = BSTheme.colors.backgroundPrimary,
        contentColor = BSTheme.colors.contentPrimary,
        tonalElevation = 0.dp,
        dragHandle = null,
    ) {
        BSPanelContent(
            title = title,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    if (!sheetState.isVisible) onDismissRequest()
                }
            },
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Bottom sheet", showBackground = true, widthDp = 390, heightDp = 600)
@Composable
private fun BSBottomSheetPreview() = BSTheme {
    var selected by remember { mutableStateOf(0) }
    BSBottomSheet("검색 결과 정렬", {}) {
        BSSelectionOption("정확도순", selected == 0, { selected = 0 })
        BSSelectionOption("발간일순", selected == 1, { selected = 1 })
        BSButton("적용", {}, Modifier.fillMaxWidth())
    }
}
