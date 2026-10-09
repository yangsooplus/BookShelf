package com.yangsooplus.bookshelf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTokens as Tokens

enum class BSStatusBannerType { Offline, Retry }

@Composable
fun BSStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    type: BSStatusBannerType = BSStatusBannerType.Offline,
) {
    Row(
        modifier.fillMaxWidth().height(56.dp)
            .background(
                if (type == BSStatusBannerType.Offline) BSTheme.colors.supportWarningAlt else BSTheme.colors.supportCriticalAlt,
                RoundedCornerShape(Tokens.radius08)
            )
            .padding(Tokens.spacing12).semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing08),
    ) {
        BSIcon(BSIcon.Offline, null, Modifier.size(20.dp))
        BasicText(
            message,
            Modifier.weight(1f),
            style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.contentPrimary)
        )
    }
}

@Composable
fun BSStateMessage(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: BSIcon = BSIcon.Book,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier.fillMaxWidth()
            .background(BSTheme.colors.backgroundPrimary, RoundedCornerShape(Tokens.radius12))
            .padding(Tokens.spacing20),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing16),
    ) {
        BSIcon(icon, null, Modifier.size(20.dp))
        BasicText(
            title,
            style = BSTheme.typography.titleMedium.copy(color = BSTheme.colors.contentPrimary)
        )
        BasicText(
            description,
            style = BSTheme.typography.bodyMedium.copy(color = BSTheme.colors.supportNormal)
        )
        action?.invoke()
    }
}

@Preview(name = "Status banner · offline / retry", showBackground = true, widthDp = 350)
@Composable
private fun BSStatusBannerPreview() = BSTheme {
    Column(verticalArrangement = Arrangement.spacedBy(Tokens.spacing12)) {
        BSStatusBanner("오프라인 · 저장한 책은 볼 수 있어요")
        BSStatusBanner("연결이 끊겼어요. 다시 시도해주세요", type = BSStatusBannerType.Retry)
    }
}

@Preview(name = "State message · initial / error", showBackground = true, widthDp = 350)
@Composable
private fun BSStateMessagePreview() = BSTheme {
    Column(verticalArrangement = Arrangement.spacedBy(Tokens.spacing12)) {
        BSStateMessage("읽고 싶은 책을 찾아보세요", "제목이나 저자로 검색할 수 있어요.")
        BSStateMessage("검색할 수 없어요", "연결을 확인하고 다시 시도해주세요.", icon = BSIcon.Offline) {
            BSButton("다시 시도", {}, Modifier.fillMaxWidth())
        }
    }
}
