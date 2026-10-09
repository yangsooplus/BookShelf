package com.yangsooplus.bookshelf.feature.book.screen.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSBottomSheet
import com.yangsooplus.bookshelf.core.designsystem.component.BSButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSButtonStyle
import com.yangsooplus.bookshelf.core.designsystem.component.BSDialog
import com.yangsooplus.bookshelf.core.designsystem.component.BSRangeSlider
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.util.formatWonPrice
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteBooksPricePanel(
    minPrice: Int?,
    maxPrice: Int?,
    upperBound: Int,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onApply: (Int, Int) -> Unit,
    onReset: () -> Unit,
) {
    val windowSize = currentWindowAdaptiveInfoV2().windowSizeClass
    val useDialog = windowSize.isWidthAtLeastBreakpoint(widthDpBreakpoint = 600)
    var selectedMin by rememberSaveable(minPrice, maxPrice, upperBound) {
        mutableFloatStateOf(value = (minPrice ?: 0).coerceIn(minimumValue = 0, maximumValue = upperBound).toFloat())
    }
    var selectedMax by rememberSaveable(minPrice, maxPrice, upperBound) {
        mutableFloatStateOf(value = (maxPrice ?: upperBound).coerceIn(minimumValue = 0, maximumValue = upperBound).toFloat())
    }
    val content: @Composable () -> Unit = {
        FavoriteBooksPriceControls(
            value = selectedMin..selectedMax,
            upperBound = upperBound,
            enabled = enabled,
            onValueChange = { range ->
                selectedMin = ((range.start / 1_000).roundToInt() * 1_000f).coerceIn(minimumValue = 0f, maximumValue = upperBound.toFloat())
                selectedMax = ((range.endInclusive / 1_000).roundToInt() * 1_000f).coerceIn(minimumValue = selectedMin, maximumValue = upperBound.toFloat())
            },
            onApply = { onApply(selectedMin.toInt(), selectedMax.toInt()) },
            onReset = onReset,
        )
    }
    if (useDialog) {
        BSDialog(title = "가격 필터", onDismissRequest = onDismiss, content = content)
    } else {
        BSBottomSheet(title = "가격 필터", onDismissRequest = onDismiss, content = content)
    }
}

@Composable
private fun FavoriteBooksPriceControls(
    value: ClosedFloatingPointRange<Float>,
    upperBound: Int,
    enabled: Boolean,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 16.dp)) {
        BasicText(
            text = "${value.start.toInt().formatWonPrice()} ~ ${value.endInclusive.toInt().formatWonPrice()}",
            modifier = Modifier.fillMaxWidth(),
            style = BSTheme.typography.titleMedium.copy(color = BSTheme.colors.contentInteractive, textAlign = TextAlign.Center),
        )
        BSRangeSlider(value = value, onValueChange = onValueChange, valueRange = 0f..upperBound.toFloat(), enabled = enabled)
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicText(text = "0원", modifier = Modifier.weight(weight = 1f), style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.supportNormal))
            BasicText(text = upperBound.formatWonPrice(), style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.supportNormal))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(space = 12.dp)) {
            BSButton(label = "초기화", onClick = onReset, modifier = Modifier.width(width = 110.dp), style = BSButtonStyle.Subtle)
            BSButton(label = "적용", onClick = onApply, modifier = Modifier.weight(weight = 1f), enabled = enabled)
        }
    }
}

@Preview(showBackground = true, widthDp = 350)
@Composable
private fun FavoriteBooksPriceControlsPreview() = BSTheme {
    FavoriteBooksPriceControls(value = 10_000f..20_000f, upperBound = 20_000, enabled = true, onValueChange = {}, onApply = {}, onReset = {})
}
