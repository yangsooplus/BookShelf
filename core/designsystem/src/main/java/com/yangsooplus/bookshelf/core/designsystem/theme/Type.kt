package com.yangsooplus.bookshelf.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp

@Immutable
data class BSTypography(
    val displayLarge: TextStyle = textStyle(32, 38, FontWeight(700)),
    val displayMedium: TextStyle = textStyle(24, 28, FontWeight(700)),
    val displaySmall: TextStyle = textStyle(20, 24, FontWeight(700)),
    val titleLarge: TextStyle = textStyle(18, 20, FontWeight(700)),
    val titleMedium: TextStyle = textStyle(16, 24, FontWeight(700)),
    val bodyLarge: TextStyle = textStyle(15, 22, FontWeight(400)),
    val bodyMedium: TextStyle = textStyle(14, 20, FontWeight(400)),
    val bodySmall: TextStyle = textStyle(13, 20, FontWeight(400)),
    val captionLarge: TextStyle = textStyle(12, 16, FontWeight(400)),
    val ui14: TextStyle = textStyle(14, 17, FontWeight(600)),
    val ui16: TextStyle = textStyle(16, 19, FontWeight(600)),
)

private fun textStyle(size: Int, height: Int, weight: FontWeight) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = height.sp,
    letterSpacing = 0.sp,
    lineBreak = LineBreak.Paragraph,
)
