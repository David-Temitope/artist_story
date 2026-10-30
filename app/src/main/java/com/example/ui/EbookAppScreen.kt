package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.formatCompactMoney

data class EbookDefinition(
    val id: String,
    val number: String,
    val title: String,
    val subtitle: String,
    val author: String = "Industry Archives",
    val price: Double,
    val coverGradient: List<Color>,
    val accentColor: Color,
    val chapters: List<EbookChapter>
)

data class EbookChapter(
    val title: String,
    val content: String
)

object EbookCatalog {
    val BOOKS: List<EbookDefinition> = EbookCatalogData.ALL_EBOOKS

    fun findBook(id: String): EbookDefinition? = BOOKS.find { it.id == id }
}

@Composable
fun EbookAppScreen(
    state: GameStateEntity,
    viewModel: GameViewModel
) {
    var activeReadingBookId by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Purchased"

    if (activeReadingBookId != null) {
        val book = EbookCatalog.findBook(activeReadingBookId!!)
        if (book != null) {
            EbookReaderScreen(
                book = book,
                onBack = { activeReadingBookId = null }
            )
        } else {
            activeReadingBookId = null
        }
    } else {
        EbookStoreScreen(
            state = state,
            viewModel = viewModel,
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it },
            onReadBook = { bookId -> activeReadingBookId = bookId }
        )
    }
}

@Composable
fun EbookStoreScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onReadBook: (String) -> Unit
) {
    var purchasingBook by remember { mutableStateOf<EbookDefinition?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E17))
    ) {
        // App Store Header Banner
        Surface(
            color = Color(0xFF131826),
            border = BorderStroke(1.dp, Color(0xFF232B42)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFFC084FC),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "eBook Store",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Elite Strategy & Industry Guides",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Cash Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E2638),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(13.dp))
                            Text(
                                text = "$${formatCompactMoney(state.cash)}",
                                color = Color(0xFF4ADE80),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val ownedCount = state.purchasedEbooks.size
                    listOf(
                        "All" to "All Titles (${EbookCatalog.BOOKS.size})",
                        "Purchased" to "My Library ($ownedCount)"
                    ).forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF1E2638),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFFA78BFA) else Color(0xFF334155)),
                            modifier = Modifier.clickable { onFilterChange(key) }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2 on a row Grid
        val displayedBooks = remember(selectedFilter, state.purchasedEbooks) {
            if (selectedFilter == "Purchased") {
                EbookCatalog.BOOKS.filter { state.purchasedEbooks.contains(it.id) }
            } else {
                EbookCatalog.BOOKS
            }
        }

        if (displayedBooks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AutoStories, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Text("No eBooks in your library yet", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Browse the eBook Store catalog to unlock invaluable career and financial knowledge.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedBooks) { book ->
                    val isOwned = state.purchasedEbooks.contains(book.id)
                    RealLifeBookCard(
                        book = book,
                        isOwned = isOwned,
                        canAfford = state.cash >= book.price,
                        onBuyClick = { purchasingBook = book },
                        onReadClick = { onReadBook(book.id) }
                    )
                }
            }
        }
    }

    // Purchase Confirmation Dialog
    purchasingBook?.let { book ->
        val canAfford = state.cash >= book.price
        AlertDialog(
            onDismissRequest = { purchasingBook = null },
            containerColor = Color(0xFF191E2E),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MenuBook, null, tint = book.accentColor)
                    Text("Acquire ${book.title}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        book.subtitle,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Edition Price:", color = Color.Gray, fontSize = 12.sp)
                        Text("$${String.format("%,.0f", book.price)}", color = book.accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Balance:", color = Color.Gray, fontSize = 12.sp)
                        Text("$${formatCompactMoney(state.cash)}", color = if (canAfford) Color(0xFF4ADE80) else Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (!canAfford) {
                        Text("⚠️ Insufficient funds to buy this master guide!", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = purchasingBook ?: return@Button
                        purchasingBook = null
                        viewModel.buyEbook(target.id)
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = book.accentColor)
                ) {
                    Text(if (canAfford) "Buy ($${formatCompactMoney(book.price)})" else "Can't Afford", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { purchasingBook = null }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun RealLifeBookCard(
    book: EbookDefinition,
    isOwned: Boolean,
    canAfford: Boolean,
    onBuyClick: () -> Unit,
    onReadClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141926)),
        border = BorderStroke(1.dp, if (isOwned) book.accentColor.copy(alpha = 0.5f) else Color(0xFF232B40)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp))
    ) {
        Column {
            // Realistic Hardcover Spine & Embossed Cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.verticalGradient(book.coverGradient))
            ) {
                // Book Spine simulated highlight on the left
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(10.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Embossed Decorative Border Box
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 14.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                        .border(1.dp, book.accentColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = book.number,
                                color = book.accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = book.accentColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Book Title & Foil Stamp
                        Column {
                            Text(
                                text = book.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = book.subtitle,
                                color = Color.LightGray.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                lineHeight = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Seal / Publisher Mark
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = book.accentColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "MASTER EDITION",
                                color = book.accentColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Owned Ribbon
                if (isOwned) {
                    Surface(
                        color = book.accentColor,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "UNLOCKED",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Action Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if (isOwned) {
                    Button(
                        onClick = onReadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                        border = BorderStroke(1.dp, book.accentColor.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(Icons.Default.AutoStories, null, tint = book.accentColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Read Book", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onBuyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = book.accentColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color.Black, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$${formatCompactMoney(book.price)}",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EbookReaderScreen(
    book: EbookDefinition,
    onBack: () -> Unit
) {
    var fontSizeMultiplier by remember { mutableStateOf(1.0f) } // 0.85f, 1.0f, 1.2f
    var themeMode by remember { mutableStateOf("DARK") } // "DARK", "PARCHMENT", "MIDNIGHT"

    val (bgCol, textCol, cardBg, dividerCol) = when (themeMode) {
        "PARCHMENT" -> listOf(Color(0xFFFBF0D9), Color(0xFF2C2216), Color(0xFFF3E4C2), Color(0xFFD6C39B))
        "MIDNIGHT" -> listOf(Color(0xFF000000), Color(0xFFE2E8F0), Color(0xFF0F172A), Color(0xFF1E293B))
        else -> listOf(Color(0xFF0F131D), Color(0xFFE6EDF3), Color(0xFF161B26), Color(0xFF263045))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgCol)
    ) {
        // Reader Top Bar
        Surface(
            color = cardBg,
            border = BorderStroke(1.dp, dividerCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textCol)
                    }
                    Column {
                        Text(
                            text = book.title,
                            color = textCol,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = book.number,
                            color = book.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Controls: Font Size and Theme Switcher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Font Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = bgCol,
                        border = BorderStroke(1.dp, dividerCol),
                        modifier = Modifier.clickable {
                            fontSizeMultiplier = when (fontSizeMultiplier) {
                                0.85f -> 1.0f
                                1.0f -> 1.2f
                                else -> 0.85f
                            }
                        }
                    ) {
                        Text(
                            text = when (fontSizeMultiplier) {
                                0.85f -> "A-"
                                1.2f -> "A+"
                                else -> "A"
                            },
                            color = textCol,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Theme Toggle
                    IconButton(
                        onClick = {
                            themeMode = when (themeMode) {
                                "DARK" -> "PARCHMENT"
                                "PARCHMENT" -> "MIDNIGHT"
                                else -> "DARK"
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (themeMode == "PARCHMENT") Icons.Default.WbSunny else Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint = book.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Reading Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // Frontispiece Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = book.accentColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, book.accentColor)
                    ) {
                        Text(
                            text = book.number.uppercase(),
                            color = book.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = book.title,
                        color = textCol,
                        fontSize = (22 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.subtitle,
                        color = textCol.copy(alpha = 0.7f),
                        fontSize = (12 * fontSizeMultiplier).sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = dividerCol)
                }
            }

            // Chapters
            items(book.chapters.size) { idx ->
                val chap = book.chapters[idx]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, dividerCol),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(book.accentColor)
                            )
                            Text(
                                text = chap.title,
                                color = book.accentColor,
                                fontSize = (15 * fontSizeMultiplier).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = chap.content,
                            color = textCol,
                            fontSize = (13 * fontSizeMultiplier).sp,
                            lineHeight = (20 * fontSizeMultiplier).sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            // Epilogue
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❖ End of Volume ❖",
                        color = book.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Mastery comes through thoughtful execution.",
                        color = textCol.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}
