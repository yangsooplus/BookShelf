package com.yangsooplus.bookshelf.core.designsystem.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalBSColors = staticCompositionLocalOf { BSColors() }
internal val LocalBSTypography = staticCompositionLocalOf { BSTypography() }

object BSTheme {
    val colors: BSColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBSColors.current

    val typography: BSTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalBSTypography.current
}

@Composable
fun BSTheme(
    colors: BSColors = BSTheme.colors,
    typography: BSTypography = BSTheme.typography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalBSColors provides colors,
        LocalBSTypography provides typography,
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = colors.contentInteractive,
            backgroundColor = colors.supportPositiveAlt,
        ),
        content = content,
    )
}
