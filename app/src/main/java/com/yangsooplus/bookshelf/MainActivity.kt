package com.yangsooplus.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yangsooplus.bookshelf.core.designsystem.theme.BSTheme
import com.yangsooplus.bookshelf.feature.book.BookEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BSTheme { BookEntry() }
        }
    }
}
