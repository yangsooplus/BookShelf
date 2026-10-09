package com.yangsooplus.bookshelf.feature.book.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yangsooplus.bookshelf.core.designsystem.component.BSBottomNavigation
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.component.BSNavigationItem
import com.yangsooplus.bookshelf.core.designsystem.component.BSNavigationRail
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.navigation.BookTab

private val navigationItems = listOf(
    BSNavigationItem(label = "검색", icon = BSIcon.Search),
    BSNavigationItem(label = "즐겨찾기", icon = BSIcon.Heart),
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal data class BookLayoutInfo(
    val useRail: Boolean,
    val paneDirective: PaneScaffoldDirective,
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun currentBookLayoutInfo(): BookLayoutInfo {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val windowSize = adaptiveInfo.windowSizeClass
    val isTabletop = adaptiveInfo.windowPosture.isTabletop
    val isCompactWidth = !windowSize.isWidthAtLeastBreakpoint(600)
    val isCompactHeight = !windowSize.isHeightAtLeastBreakpoint(480)
    val isExpandedWidth = windowSize.isWidthAtLeastBreakpoint(840)

    // 폭·높이가 Compact이거나 테이블탑이면 하단 탭, 그 외에는 Rail을 사용한다.
    val useRail = !isCompactWidth && !isCompactHeight && !isTabletop
    // Expanded 창에서는 목록과 상세를 나란히 표시하고, 테이블탑에서는 한 화면씩 표시한다.
    val showListAndDetailSideBySide = isExpandedWidth && !isTabletop

    val paneDirective = calculatePaneScaffoldDirective(adaptiveInfo).copy(
        maxHorizontalPartitions = if (showListAndDetailSideBySide) 2 else 1,
        // 테이블탑의 위·아래 콘텐츠 배치는 각 화면에서 구성한다.
        maxVerticalPartitions = 1,
    )
    return BookLayoutInfo(useRail = useRail, paneDirective = paneDirective)
}

@Composable
internal fun BookAdaptiveLayout(
    useRail: Boolean,
    selectedTab: BookTab,
    onSelectTab: (BookTab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    val selectTab: (Int) -> Unit = { onSelectTab(BookTab.entries[it]) }

    Box(
        modifier = modifier.fillMaxSize()
            .background(BSTheme.colors.backgroundSecondary)
            .safeDrawingPadding(),
    ) {
        if (useRail) {
            Row(modifier = Modifier.fillMaxSize()) {
                BSNavigationRail(navigationItems, selectedTab.ordinal, selectTab)
                content(Modifier.weight(1f))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                content(Modifier.weight(1f))
                BSBottomNavigation(navigationItems, selectedTab.ordinal, selectTab)
            }
        }
    }
}
