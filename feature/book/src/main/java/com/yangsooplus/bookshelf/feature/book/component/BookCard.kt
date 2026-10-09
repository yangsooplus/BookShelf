package com.yangsooplus.bookshelf.feature.book.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yangsooplus.bookshelf.core.designsystem.component.BSFavoriteButton
import com.yangsooplus.bookshelf.core.designsystem.component.BSIcon
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val bookDateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Composable
internal fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp)
            .clip(shape)
            .background(BSTheme.colors.backgroundPrimary)
            .border(1.dp, BSTheme.colors.borderPrimary, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookCover(book.thumbnailUrl)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(Modifier.fillMaxWidth()) {
                BasicText(
                    book.title,
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(end = 56.dp),
                    style = BSTheme.typography.titleMedium.copy(color = BSTheme.colors.contentPrimary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(Modifier.matchParentSize()) {
                    BSFavoriteButton(
                        checked = book.isFavorite,
                        onCheckedChange = onFavoriteChange,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .requiredSize(48.dp),
                    )
                }
            }
            BasicText(
                "${
                    book.authors.joinToString(", ").ifBlank { "저자 미상" }
                } · ${book.publisher.ifBlank { "출판사 미상" }}",
                style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.supportNormal),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BasicText(
                    if (book.publishedDate == LocalDate.MIN) "출간일 미상"
                    else book.publishedDate.format(bookDateFormat),
                    Modifier.width(76.dp),
                    style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.supportNormal),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val price = book.price.representPrice
                BasicText(
                    if (price < 0) "가격 정보 없음" else "${
                        NumberFormat.getIntegerInstance(Locale.KOREA).format(price)
                    }원",
                    Modifier.weight(1f),
                    style = BSTheme.typography.ui16.copy(
                        color = BSTheme.colors.contentPrimary,
                        textAlign = TextAlign.End,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BookCover(thumbnailUrl: String) {
    var imageLoaded by remember(thumbnailUrl) { mutableStateOf(false) }
    Box(
        Modifier
            .size(80.dp, 112.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(BSTheme.colors.backgroundInputNormal),
        contentAlignment = Alignment.Center,
    ) {
        if (!imageLoaded) {
            BSIcon(
                BSIcon.Image,
                "표지 이미지 없음",
                Modifier.size(32.dp),
                tint = BSTheme.colors.contentSecondary
            )
        }
        if (thumbnailUrl.isNotBlank()) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "도서 표지",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { imageLoaded = true },
                onLoading = { imageLoaded = false },
                onError = { imageLoaded = false },
            )
        }
    }
}

@Composable
internal fun BookCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp)
            .background(BSTheme.colors.backgroundPrimary, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(Modifier.size(80.dp, 112.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBlock(Modifier.size(176.dp, 20.dp))
            SkeletonBlock(Modifier.size(124.dp, 12.dp))
            SkeletonBlock(Modifier.size(92.dp, 12.dp))
            SkeletonBlock(Modifier.size(80.dp, 20.dp))
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier) {
    Box(
        modifier = modifier.background(
            BSTheme.colors.backgroundInputNormal,
            RoundedCornerShape(4.dp)
        )
    )
}

@Preview(name = "BookCard")
@Composable
private fun BookCardPreview() = BSTheme {
    BookCard(
        book = Book(
            id = "9780000000001",
            title = "생각의 기록",
            contents = "",
            url = "",
            authors = listOf("김서윤"),
            publisher = "문장숲",
            publishedDate = LocalDate.of(2026, 9, 1),
            price = BookPrice(16_000, 14_400),
            thumbnailUrl = "",
            isbn = "9780000000001",
            translators = emptyList(),
            isFavorite = true,
        ),
        onClick = {},
        onFavoriteChange = {},
    )
}

@Preview(name = "BookCard · Skeleton")
@Composable
private fun BookCardSkeletonPreview() = BSTheme {
    BookCardSkeleton()
}
