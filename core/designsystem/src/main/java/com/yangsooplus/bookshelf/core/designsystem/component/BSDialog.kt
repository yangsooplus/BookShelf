package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

@Composable
fun BSDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .padding(Tokens.spacing20)
                .widthIn(max = 390.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Tokens.radius20))
                .background(BSTheme.colors.backgroundPrimary),
        ) {
            BSPanelContent(title, onDismissRequest, content)
        }
    }
}

@Preview(name = "Dialog", showBackground = true, widthDp = 600, heightDp = 600)
@Composable
private fun BSDialogPreview() = BSTheme {
    var selected by remember { mutableStateOf(0) }
    BSDialog("검색 결과 정렬", {}) {
        BSSelectionOption("정확도순", selected == 0, { selected = 0 })
        BSSelectionOption("발간일순", selected == 1, { selected = 1 })
        BSButton("적용", {}, Modifier.fillMaxWidth())
    }
}
