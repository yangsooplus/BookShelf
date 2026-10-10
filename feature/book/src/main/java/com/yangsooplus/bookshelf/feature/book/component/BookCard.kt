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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yangsooplus.bookshelf.core.designsystem.component.BSFavoriteButton
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.domain.book.model.Book
import com.yangsooplus.bookshelf.domain.book.model.BookPrice
import com.yangsooplus.bookshelf.feature.book.util.formatAuthorPublisher
import com.yangsooplus.bookshelf.feature.book.util.formatPublishedDate
import com.yangsooplus.bookshelf.feature.book.util.formatPrice
import java.time.LocalDate

@Composable
internal fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size = 12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp)
            .clip(shape = shape)
            .background(color = BSTheme.colors.backgroundPrimary)
            .border(width = 1.dp, color = BSTheme.colors.borderPrimary, shape = shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        BookCover(thumbnailUrl = book.thumbnailUrl)

        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BasicText(
                    text = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(end = 56.dp),
                    style = BSTheme.typography.titleMedium.copy(color = BSTheme.colors.contentPrimary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(modifier = Modifier.matchParentSize()) {
                    BSFavoriteButton(
                        checked = book.isFavorite,
                        onCheckedChange = onFavoriteChange,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .requiredSize(size = 48.dp),
                    )
                }
            }
            BasicText(
                text = book.formatAuthorPublisher(),
                style = BSTheme.typography.bodySmall.copy(color = BSTheme.colors.supportNormal),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                BasicText(
                    text = book.formatPublishedDate(),
                    modifier = Modifier.width(width = 76.dp),
                    style = BSTheme.typography.captionLarge.copy(color = BSTheme.colors.supportNormal),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(
                    text = book.formatPrice(),
                    modifier = Modifier.weight(weight = 1f),
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
internal fun BookCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp)
            .background(color = BSTheme.colors.backgroundPrimary, shape = RoundedCornerShape(size = 12.dp))
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(modifier = Modifier.size(width = 80.dp, height = 112.dp))
        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            SkeletonBlock(modifier = Modifier.size(width = 176.dp, height = 20.dp))
            SkeletonBlock(modifier = Modifier.size(width = 124.dp, height = 12.dp))
            SkeletonBlock(modifier = Modifier.size(width = 92.dp, height = 12.dp))
            SkeletonBlock(modifier = Modifier.size(width = 80.dp, height = 20.dp))
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier) {
    Box(
        modifier = modifier.background(
            color = BSTheme.colors.backgroundInputNormal,
            shape = RoundedCornerShape(size = 4.dp)
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
            price = BookPrice(regularPrice = 16_000, salePrice = 14_400),
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
