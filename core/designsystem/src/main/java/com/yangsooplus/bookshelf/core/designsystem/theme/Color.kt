package com.yangsooplus.bookshelf.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class BSColors(
    val contentPrimary: Color = BSTokens.Neutral900,
    val contentSecondary: Color = BSTokens.Neutral500,
    val contentPlaceholder: Color = BSTokens.Neutral400,
    val contentDisabled: Color = BSTokens.Neutral200,
    val contentInversePrimary: Color = BSTokens.Common100,
    val contentInteractive: Color = BSTokens.Cyan800,
    val backgroundPrimary: Color = BSTokens.Common100,
    val backgroundSecondary: Color = BSTokens.Navy10,
    val backgroundInputNormal: Color = BSTokens.Neutral50,
    val borderPrimary: Color = BSTokens.Neutral100,
    val borderSelected: Color = BSTokens.Cyan200,
    val borderPrimaryActivated: Color = BSTokens.Cyan800,
    val buttonPrimary: Color = BSTokens.Cyan800,
    val buttonPrimaryHover: Color = BSTokens.Cyan900,
    val buttonDefault: Color = BSTokens.Common100,
    val buttonDefaultOutline: Color = BSTokens.Neutral100,
    val buttonSecondary: Color = BSTokens.Neutral50,
    val buttonDisabled: Color = BSTokens.Neutral10,
    val supportFavorite: Color = BSTokens.LivelyRed800,
    val supportFavoriteAlt: Color = BSTokens.Neutral500,
    val supportPositiveAlt: Color = BSTokens.Cyan100,
    val supportWarningAlt: Color = BSTokens.Yellow50,
    val supportCritical: Color = BSTokens.LivelyRed800,
    val supportCriticalAlt: Color = BSTokens.LivelyRed100,
    val supportNormal: Color = BSTokens.Neutral600,
)
