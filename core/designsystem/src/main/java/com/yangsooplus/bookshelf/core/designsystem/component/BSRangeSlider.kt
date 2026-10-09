package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val activeColor = if (enabled) BSTheme.colors.buttonPrimary else BSTheme.colors.contentDisabled
    val inactiveColor = BSTheme.colors.borderPrimary
    val layoutDirection = LocalLayoutDirection.current


    RangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().height(48.dp),
        valueRange = valueRange,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        startThumb = { RangeSliderThumb(enabled) },
        endThumb = { RangeSliderThumb(enabled) },
        track = { state ->
            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val span = valueRange.endInclusive - valueRange.start
                val start =
                    if (span > 0f) (state.activeRangeStart - valueRange.start) / span else 0f
                val end = if (span > 0f) (state.activeRangeEnd - valueRange.start) / span else 0f
                val cornerRadius = CornerRadius(2.dp.toPx())
                scale(
                    scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f,
                    scaleY = 1f
                ) {
                    drawRoundRect(inactiveColor, cornerRadius = cornerRadius)
                    if (end > start) {
                        drawRoundRect(
                            color = activeColor,
                            topLeft = Offset(size.width * start, 0f),
                            size = Size(size.width * (end - start), size.height),
                            cornerRadius = cornerRadius,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun RangeSliderThumb(enabled: Boolean) {
    Spacer(
        modifier = Modifier
            .size(48.dp)
            .padding(14.dp)
            .background(BSTheme.colors.backgroundPrimary, CircleShape)
            .border(
                width = 2.dp,
                color = if (enabled) BSTheme.colors.buttonPrimary else BSTheme.colors.contentDisabled,
                shape = CircleShape,
            ),
    )
}

internal class BSRangeSliderPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@Preview(name = "Range slider", showBackground = true, widthDp = 350)
@Composable
private fun BSRangeSliderPreview(
    @PreviewParameter(BSRangeSliderPreviewParameterProvider::class) enabled: Boolean,
) = BSTheme {
    var value by remember { mutableStateOf(10_000f..20_000f) }
    BSRangeSlider(value, { value = it }, valueRange = 0f..20_000f, enabled = enabled)
}
