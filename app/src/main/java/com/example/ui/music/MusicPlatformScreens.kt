package com.example.ui.music

import com.example.ui.PlayerAvatarDisplay

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.AvatarWidget
import com.example.R
import com.example.data.ChartSong
import com.example.data.GameData
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.Song
import com.example.ui.ArtistFaceAvatar
import kotlin.random.Random

// =========================================================================
// SHARED ARTWORK PHOTO & COVER GENERATOR
// =========================================================================
@Composable
fun ProceduralCoverArt(
    title: String,
    genre: String = "Electronic",
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF1DB954)
) {
    val coverRes = remember(title, genre) {
        com.example.ui.AlbumCoverRegistry.getCoverDrawable(title, genre)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = coverRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(topEnd = 4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = title.take(2).uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// =========================================================================
// SHARED MINI PLAYER BAR
// =========================================================================
@Composable
fun SharedPlatformMiniPlayer(
    trackTitle: String,
    artistName: String,
    genre: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF18181A),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProceduralCoverArt(
                title = trackTitle,
                genre = genre,
                modifier = Modifier.size(44.dp),
                accentColor = accentColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackTitle,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = artistName,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isPlaying) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(14.dp, 8.dp, 16.dp, 10.dp)
                    heights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(h)
                                .background(accentColor, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =========================================================================
// 1. SONGIFY (Inspired by Spotify - Discovery, Daily Mixes, Dark & Neon Green)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongifyScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenOtherPlatform: (String) -> Unit = {}
) {
    val songifyColor = Color(0xFF1DB954)
    val songifyDarkBg = Color(0xFF121212)
    val songifyCardBg = Color(0xFF1E1E1E)

    val playerUploadedSongs = state.songs.filter { it.isUploadedMap.containsKey("songify") }
    val playerUnuploadedSongs = state.songs.filter { !it.isUploadedMap.containsKey("songify") }
    val totalSongifyStreams = playerUploadedSongs.sumOf { it.isUploadedMap["songify"] ?: 0L }
    val monthlyListeners = (totalSongifyStreams * 0.75f + state.fans * 0.85f).toLong().coerceAtLeast(0L)

    val allChartSongs = state.soundstreamSongs.sortedByDescending { it.weeklyStreams }
    val top10Chart = allChartSongs.take(10)
    val newReleases = allChartSongs.filter { it.weeksOnChart <= 3 || it.releasedWeek == state.currentWeek }.take(8)

    var selectedTab by remember { mutableStateOf("Home") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenreFilter by remember { mutableStateOf<String?>(null) }
    var currentTrackTitle by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.title ?: top10Chart.firstOrNull()?.title ?: "Midnight Dreams") }
    var currentArtistName by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.let { state.artistName } ?: top10Chart.firstOrNull()?.artistName ?: "Vespera") }
    var currentGenre by remember { mutableStateOf("Pop") }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = songifyDarkBg,
        bottomBar = {
            Column {
                SharedPlatformMiniPlayer(
                    trackTitle = currentTrackTitle,
                    artistName = currentArtistName,
                    genre = currentGenre,
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    accentColor = songifyColor
                )
                NavigationBar(
                    containerColor = Color(0xFF000000),
                    contentColor = Color.White
                ) {
                    val tabs = listOf(
                        Triple("Home", Icons.Default.Home, "home_tab"),
                        Triple("Search", Icons.Default.Search, "search_tab"),
                        Triple("Library", Icons.Default.LibraryMusic, "library_tab"),
                        Triple("Premium", Icons.Default.WorkspacePremium, "premium_tab")
                    )
                    tabs.forEach { (tab, icon, testTag) ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab,
                                    tint = if (selectedTab == tab) songifyColor else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) Color.White else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag(testTag)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F381E),
                            songifyDarkBg,
                            songifyDarkBg
                        )
                    )
                )
        ) {
            when (selectedTab) {
                "Home" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PlayerAvatarDisplay(state = state, size = 42.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Good evening,",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = state.artistName,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF222222),
                                    border = BorderStroke(0.5.dp, Color.DarkGray)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Headphones,
                                            contentDescription = null,
                                            tint = songifyColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${String.format("%,d", monthlyListeners)} listeners",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Quick Tiles (2 Columns of player songs + top hits)
                        item {
                            val quickItems = (playerUploadedSongs.map { it.title to state.artistName } +
                                    top10Chart.map { it.title to it.artistName }).distinctBy { it.first }.take(6)
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.height(160.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false
                            ) {
                                items(quickItems) { (title, artist) ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = songifyCardBg,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .clickable {
                                                currentTrackTitle = title
                                                currentArtistName = artist
                                                isPlaying = true
                                            }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            ProceduralCoverArt(
                                                title = title,
                                                genre = "Pop",
                                                modifier = Modifier.size(48.dp),
                                                accentColor = songifyColor
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = title,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // TODAY'S TOP HITS
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Today'''s Top Hits",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Week ${state.currentWeek}",
                                    color = songifyColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        items(top10Chart) { song ->
                            val isPlayer = song.isPlayer || song.artistName == state.artistName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPlayer) Color(0xFF14301B) else songifyCardBg,
                                border = if (isPlayer) BorderStroke(1.dp, songifyColor) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentTrackTitle = song.title
                                        currentArtistName = song.artistName
                                        currentGenre = song.genreName
                                        isPlaying = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${song.currentRank}",
                                        color = if (song.currentRank <= 3) songifyColor else Color.LightGray,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    ProceduralCoverArt(
                                        title = song.title,
                                        genre = song.genreName,
                                        modifier = Modifier.size(44.dp),
                                        accentColor = songifyColor
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = song.title,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (isPlayer) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = songifyColor,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "YOU",
                                                        color = Color.Black,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${song.artistName} • ${song.genreName}",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${String.format("%,d", song.weeklyStreams)}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "streams",
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        // NEW MUSIC FRIDAY (Fresh Drops)
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "New Music Friday ⚡",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(newReleases) { song ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = songifyCardBg,
                                        modifier = Modifier
                                            .width(140.dp)
                                            .clickable {
                                                currentTrackTitle = song.title
                                                currentArtistName = song.artistName
                                                currentGenre = song.genreName
                                                isPlaying = true
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            ProceduralCoverArt(
                                                title = song.title,
                                                genre = song.genreName,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(120.dp),
                                                accentColor = songifyColor
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = song.title,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = song.artistName,
                                                color = Color.LightGray,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "NEW",
                                                    color = songifyColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                Text(
                                                    text = "${String.format("%.1f", song.rating)}⭐",
                                                    color = Color.Yellow,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // MADE FOR YOU - DAILY MIXES
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Made For You",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            val mixes = listOf(
                                Triple("Daily Mix 1", "Pop & Electro Anthems", listOf("Pop", "Electronic")),
                                Triple("Daily Mix 2", "Hip Hop & Street Heat", listOf("Hip Hop", "Rap")),
                                Triple("Daily Mix 3", "Chill Vibes & Acoustic", listOf("Indie", "R&B")),
                                Triple("Daily Mix 4", "Rock & Heavy Riffs", listOf("Rock", "Alternative"))
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(mixes) { (name, desc, genres) ->
                                    val mixSongs = allChartSongs.filter { cs -> genres.any { g -> cs.genreName.contains(g, ignoreCase = true) } }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = songifyCardBg,
                                        modifier = Modifier
                                            .width(150.dp)
                                            .clickable {
                                                val firstSong = mixSongs.firstOrNull()
                                                if (firstSong != null) {
                                                    currentTrackTitle = firstSong.title
                                                    currentArtistName = firstSong.artistName
                                                    currentGenre = firstSong.genreName
                                                    isPlaying = true
                                                }
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(130.dp)
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(songifyColor.copy(alpha = 0.8f), Color(0xFF0A2E16))
                                                        ),
                                                        RoundedCornerShape(6.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        imageVector = Icons.Default.GraphicEq,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Text(
                                                        text = name,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = name,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = desc,
                                                color = Color.Gray,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // YOUR SONGIFY ARTIST CATALOG
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF162419),
                                border = BorderStroke(1.dp, songifyColor.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = songifyColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Your Songify Catalog",
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "${playerUploadedSongs.size} Tracks Live",
                                            color = songifyColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (playerUploadedSongs.isEmpty()) {
                                        Text(
                                            text = "You haven'''t uploaded any songs to Songify yet! Head over to the Library tab to distribute your master recordings.",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        playerUploadedSongs.forEach { song ->
                                            val streams = song.isUploadedMap["songify"] ?: 0L
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "• ${song.title}",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "${String.format("%,d", streams)} plays",
                                                    color = songifyColor,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                "Search" -> {
                    val filteredSongs = remember(searchQuery, selectedGenreFilter, allChartSongs, state.songs) {
                        val pool = (playerUploadedSongs.map {
                            ChartSong(
                                id = it.id,
                                title = it.title,
                                artistName = state.artistName,
                                artistId = "player",
                                genreName = GameData.GENRES.find { g -> g.id == it.genreId }?.name ?: "Pop",
                                rating = it.rating,
                                weeklyStreams = it.isUploadedMap["songify"] ?: 0L,
                                totalStreams = it.totalStreams,
                                weeksOnChart = 1,
                                peakRank = 1,
                                isPlayer = true,
                                releasedWeek = it.weekCreated,
                                releasedYear = it.yearCreated
                            )
                        } + allChartSongs).distinctBy { it.title + it.artistName }

                        if (searchQuery.isNotBlank()) {
                            pool.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                        it.artistName.contains(searchQuery, ignoreCase = true) ||
                                        it.genreName.contains(searchQuery, ignoreCase = true)
                            }
                        } else if (selectedGenreFilter != null) {
                            pool.filter { it.genreName.contains(selectedGenreFilter!!, ignoreCase = true) }
                        } else {
                            pool
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Search",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search songs, artists, or genres...", color = Color.Gray, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.LightGray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty() || selectedGenreFilter != null) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        selectedGenreFilter = null
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF242424),
                                unfocusedContainerColor = Color(0xFF242424),
                                focusedBorderColor = songifyColor,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (searchQuery.isBlank() && selectedGenreFilter == null) {
                            Text(
                                text = "Browse All Genres",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            val categories = listOf(
                                "Pop" to Color(0xFF8C1932),
                                "Hip Hop" to Color(0xFFBA5D07),
                                "Electronic" to Color(0xFF1E3264),
                                "Indie" to Color(0xFF608108),
                                "Rock" to Color(0xFFE91429),
                                "R&B" to Color(0xFF8D67AB),
                                "Afro" to Color(0xFFD84000),
                                "Chill" to Color(0xFF477D95)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(categories) { (cat, color) ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = color,
                                        modifier = Modifier
                                            .height(70.dp)
                                            .clickable {
                                                selectedGenreFilter = cat
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(10.dp),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Text(
                                                text = cat,
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedGenreFilter != null) "Genre: $selectedGenreFilter" else "Search Results (${filteredSongs.size})",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selectedGenreFilter != null) {
                                    TextButton(onClick = { selectedGenreFilter = null }) {
                                        Text("Show All", color = songifyColor, fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredSongs) { song ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = songifyCardBg,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                currentTrackTitle = song.title
                                                currentArtistName = song.artistName
                                                currentGenre = song.genreName
                                                isPlaying = true
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            ProceduralCoverArt(
                                                title = song.title,
                                                genre = song.genreName,
                                                modifier = Modifier.size(42.dp),
                                                accentColor = songifyColor
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = song.title,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${song.artistName} • ${song.genreName}",
                                                    color = Color.LightGray,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            IconButton(onClick = {
                                                currentTrackTitle = song.title
                                                currentArtistName = song.artistName
                                                currentGenre = song.genreName
                                                isPlaying = true
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = songifyColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Library" -> {
                    var libraryFilter by remember { mutableStateOf("Uploaded") }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Your Library",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Uploaded", "Distribute", "Albums").forEach { f ->
                                val active = libraryFilter == f
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (active) songifyColor else Color(0xFF242424),
                                    modifier = Modifier.clickable { libraryFilter = f }
                                ) {
                                    Text(
                                        text = f,
                                        color = if (active) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        when (libraryFilter) {
                            "Uploaded" -> {
                                if (playerUploadedSongs.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.CloudOff,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "No songs uploaded to Songify yet",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Switch to the 'Distribute' tab to upload your master tracks.",
                                                color = Color.Gray,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(playerUploadedSongs) { song ->
                                            val streams = song.isUploadedMap["songify"] ?: 0L
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = songifyCardBg,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        currentTrackTitle = song.title
                                                        currentArtistName = state.artistName
                                                        isPlaying = true
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    ProceduralCoverArt(
                                                        title = song.title,
                                                        genre = GameData.GENRES.find { it.id == song.genreId }?.name ?: "Pop",
                                                        modifier = Modifier.size(44.dp),
                                                        accentColor = songifyColor
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = song.title,
                                                            color = Color.White,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${String.format("%,d", streams)} total plays • Rating: ${String.format("%.1f", song.rating)}⭐",
                                                            color = songifyColor,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                    IconButton(onClick = {
                                                        currentTrackTitle = song.title
                                                        currentArtistName = state.artistName
                                                        isPlaying = true
                                                    }) {
                                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = songifyColor)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            "Distribute" -> {
                                if (playerUnuploadedSongs.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = songifyColor,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "All your songs are live on Songify!",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(playerUnuploadedSongs) { song ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = songifyCardBg,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    ProceduralCoverArt(
                                                        title = song.title,
                                                        genre = GameData.GENRES.find { it.id == song.genreId }?.name ?: "Pop",
                                                        modifier = Modifier.size(44.dp),
                                                        accentColor = songifyColor
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = song.title,
                                                            color = Color.White,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Fee: $1,500 • Rating: ${String.format("%.1f", song.rating)}⭐",
                                                            color = Color.LightGray,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                    Button(
                                                        onClick = { viewModel.uploadSong(song.id, "songify") },
                                                        colors = ButtonDefaults.buttonColors(containerColor = songifyColor),
                                                        shape = RoundedCornerShape(6.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text("Distribute", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            "Albums" -> {
                                if (state.albums.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No released albums yet", color = Color.Gray, fontSize = 13.sp)
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(state.albums) { album ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = songifyCardBg,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    ProceduralCoverArt(
                                                        title = album.title,
                                                        genre = "Pop",
                                                        modifier = Modifier.size(48.dp),
                                                        accentColor = songifyColor
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = album.title,
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${album.songIds.size} Tracks • Released Year ${album.yearCreated}",
                                                            color = Color.Gray,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "songify", "Songify", songifyColor, songifyDarkBg, songifyCardBg, 150000.0)
                }
            }
        }
    }
}

// =========================================================================
// 2. MEGAPHONE (Inspired by Apple Music - Crimson, Editorial, Lossless)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MegaphoneScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenOtherPlatform: (String) -> Unit = {}
) {
    val megaphoneColor = Color(0xFFFC3C44)
    val megaphoneDarkBg = Color(0xFF000000)
    val megaphoneCardBg = Color(0xFF1C1C1E)

    val playerUploadedSongs = state.songs.filter { it.isUploadedMap.containsKey("megaphone") }
    val playerUnuploadedSongs = state.songs.filter { !it.isUploadedMap.containsKey("megaphone") }
    val totalMegaphoneStreams = playerUploadedSongs.sumOf { it.isUploadedMap["megaphone"] ?: 0L }

    val allChartSongs = state.soundstreamSongs.sortedByDescending { it.weeklyStreams }
    val heroSong = allChartSongs.firstOrNull()
    val top10Chart = allChartSongs.take(10)
    val spatialAudioTracks = allChartSongs.filter { it.rating >= 4.2f }.take(6)

    var selectedTab by remember { mutableStateOf("Listen Now") }
    var searchQuery by remember { mutableStateOf("") }
    var currentTrackTitle by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.title ?: heroSong?.title ?: "Symphony of Light") }
    var currentArtistName by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.let { state.artistName } ?: heroSong?.artistName ?: "Travis Scottie") }
    var currentGenre by remember { mutableStateOf("Pop") }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = megaphoneDarkBg,
        bottomBar = {
            Column {
                SharedPlatformMiniPlayer(
                    trackTitle = currentTrackTitle,
                    artistName = currentArtistName,
                    genre = currentGenre,
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    accentColor = megaphoneColor
                )
                NavigationBar(
                    containerColor = Color(0xFF141416),
                    contentColor = Color.White
                ) {
                    val tabs = listOf(
                        Triple("Listen Now", Icons.Default.PlayCircleFilled, "listen_tab"),
                        Triple("Browse", Icons.Default.GridView, "browse_tab"),
                        Triple("Radio", Icons.Default.Radio, "radio_tab"),
                        Triple("Premium", Icons.Default.WorkspacePremium, "premium_tab")
                    )
                    tabs.forEach { (tab, icon, testTag) ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab,
                                    tint = if (selectedTab == tab) megaphoneColor else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) megaphoneColor else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            modifier = Modifier.testTag(testTag)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(megaphoneDarkBg)
        ) {
            when (selectedTab) {
                "Listen Now" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Listen Now",
                                        color = Color.White,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Apple Lossless & Spatial Audio",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                                PlayerAvatarDisplay(state = state, size = 42.dp)
                            }
                        }

                        // HERO CARD: #1 TRACK OF THE WEEK
                        if (heroSong != null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = megaphoneCardBg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentTrackTitle = heroSong.title
                                            currentArtistName = heroSong.artistName
                                            currentGenre = heroSong.genreName
                                            isPlaying = true
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "FEATURED TRACK OF THE WEEK",
                                            color = megaphoneColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = heroSong.title,
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "by ${heroSong.artistName} • #1 Worldwide",
                                            color = Color.LightGray,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        ProceduralCoverArt(
                                            title = heroSong.title,
                                            genre = heroSong.genreName,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(170.dp),
                                            accentColor = megaphoneColor
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = Color(0xFF333333),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "SPATIAL AUDIO",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${String.format("%,d", heroSong.weeklyStreams)} weekly streams",
                                                    color = Color.Gray,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    currentTrackTitle = heroSong.title
                                                    currentArtistName = heroSong.artistName
                                                    currentGenre = heroSong.genreName
                                                    isPlaying = true
                                                },
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .background(megaphoneColor, CircleShape)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // TOP 10 EDITORIAL CHARTS
                        item {
                            Text(
                                text = "Top 10 Global Tracks",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(top10Chart) { song ->
                            val isPlayer = song.isPlayer || song.artistName == state.artistName
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPlayer) Color(0xFF2A1215) else megaphoneCardBg,
                                border = if (isPlayer) BorderStroke(1.dp, megaphoneColor) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentTrackTitle = song.title
                                        currentArtistName = song.artistName
                                        currentGenre = song.genreName
                                        isPlaying = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${song.currentRank}",
                                        color = if (song.currentRank <= 3) megaphoneColor else Color.LightGray,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    ProceduralCoverArt(
                                        title = song.title,
                                        genre = song.genreName,
                                        modifier = Modifier.size(44.dp),
                                        accentColor = megaphoneColor
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = song.title,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (isPlayer) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = megaphoneColor,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "YOU",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${song.artistName} • ${song.genreName}",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        text = "${String.format("%,d", song.weeklyStreams)}",
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // SPATIAL AUDIO SHOWCASE
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Spatial Audio Masterpieces",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(spatialAudioTracks) { song ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = megaphoneCardBg,
                                        modifier = Modifier
                                            .width(135.dp)
                                            .clickable {
                                                currentTrackTitle = song.title
                                                currentArtistName = song.artistName
                                                currentGenre = song.genreName
                                                isPlaying = true
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            ProceduralCoverArt(
                                                title = song.title,
                                                genre = song.genreName,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(115.dp),
                                                accentColor = megaphoneColor
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = song.title,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = song.artistName,
                                                color = Color.Gray,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // YOUR MEGAPHONE CATALOG
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = megaphoneCardBg,
                                border = BorderStroke(1.dp, megaphoneColor.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Your Megaphone Catalog",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${playerUploadedSongs.size} Songs Live",
                                            color = megaphoneColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (playerUploadedSongs.isEmpty()) {
                                        Text(
                                            text = "No songs distributed to Megaphone yet. Tap below to distribute your masters for $1,000.",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        playerUploadedSongs.forEach { song ->
                                            val streams = song.isUploadedMap["megaphone"] ?: 0L
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "• ${song.title}",
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "${String.format("%,d", streams)} plays",
                                                    color = megaphoneColor,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    if (playerUnuploadedSongs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                val first = playerUnuploadedSongs.firstOrNull()
                                                if (first != null) viewModel.uploadSong(first.id, "megaphone")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = megaphoneColor),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Distribute '${playerUnuploadedSongs.first().title}' ($1,000)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                "Browse" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Browse Charts",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // City Charts
                        item {
                            Text("City Charts", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val cities = listOf("New York City", "London", "Lagos", "Tokyo", "Paris", "Toronto")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(cities) { city ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = megaphoneCardBg,
                                        modifier = Modifier
                                            .width(130.dp)
                                            .height(80.dp)
                                            .clickable {
                                                val s = allChartSongs.randomOrNull()
                                                if (s != null) {
                                                    currentTrackTitle = s.title
                                                    currentArtistName = s.artistName
                                                    isPlaying = true
                                                }
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(10.dp),
                                            contentAlignment = Alignment.BottomStart
                                        ) {
                                            Column {
                                                Text("TOP 25", color = megaphoneColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                Text(city, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("Global Top 30 Leaderboard", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        items(allChartSongs.take(30)) { song ->
                            val isPlayer = song.isPlayer || song.artistName == state.artistName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPlayer) Color(0xFF2A1215) else megaphoneCardBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentTrackTitle = song.title
                                        currentArtistName = song.artistName
                                        currentGenre = song.genreName
                                        isPlaying = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${song.currentRank}",
                                        color = if (song.currentRank <= 3) megaphoneColor else Color.LightGray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    ProceduralCoverArt(
                                        title = song.title,
                                        genre = song.genreName,
                                        modifier = Modifier.size(40.dp),
                                        accentColor = megaphoneColor
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${song.artistName} • ${song.genreName}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Text("${String.format("%,d", song.weeklyStreams)}", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                "Radio" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Megaphone 1 Radio", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF220A0D),
                            border = BorderStroke(1.dp, megaphoneColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.Red, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("LIVE NOW", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("The Global Hit Countdown", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Broadcasting worldwide with Zane Lowe & Top Guest Artists", color = Color.LightGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val s = allChartSongs.firstOrNull()
                                        if (s != null) {
                                            currentTrackTitle = s.title
                                            currentArtistName = s.artistName
                                            isPlaying = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = megaphoneColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Radio, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen Live", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "megaphone", "Megaphone", megaphoneColor, megaphoneDarkBg, megaphoneCardBg, 100000.0)
                }

                "Search" -> {
                    val filteredSongs = remember(searchQuery, allChartSongs, state.songs) {
                        val pool = (playerUploadedSongs.map {
                            ChartSong(
                                id = it.id,
                                title = it.title,
                                artistName = state.artistName,
                                artistId = "player",
                                genreName = GameData.GENRES.find { g -> g.id == it.genreId }?.name ?: "Pop",
                                rating = it.rating,
                                weeklyStreams = it.isUploadedMap["megaphone"] ?: 0L,
                                totalStreams = it.totalStreams,
                                weeksOnChart = 1,
                                peakRank = 1,
                                isPlayer = true,
                                releasedWeek = it.weekCreated,
                                releasedYear = it.yearCreated
                            )
                        } + allChartSongs).distinctBy { it.title + it.artistName }

                        if (searchQuery.isNotBlank()) {
                            pool.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                        it.artistName.contains(searchQuery, ignoreCase = true) ||
                                        it.genreName.contains(searchQuery, ignoreCase = true)
                            }
                        } else pool
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Search", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Artists, Songs, Lyrics and more...", color = Color.Gray, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.LightGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = megaphoneCardBg,
                                unfocusedContainerColor = megaphoneCardBg,
                                focusedBorderColor = megaphoneColor,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredSongs) { song ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = megaphoneCardBg,
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        currentTrackTitle = song.title
                                        currentArtistName = song.artistName
                                        currentGenre = song.genreName
                                        isPlaying = true
                                    }
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        ProceduralCoverArt(title = song.title, genre = song.genreName, modifier = Modifier.size(42.dp), accentColor = megaphoneColor)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("${song.artistName} • ${song.genreName}", color = Color.LightGray, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = {
                                            currentTrackTitle = song.title
                                            currentArtistName = song.artistName
                                            isPlaying = true
                                        }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = megaphoneColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. JAMS (Inspired by Audiomack / Boomplay - Navy Blue & Electric Cyan)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamsScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenOtherPlatform: (String) -> Unit = {}
) {
    val jamsColor = Color(0xFF00E5FF)
    val jamsDarkBg = Color(0xFF080E1A)
    val jamsCardBg = Color(0xFF101B2E)

    val playerUploadedSongs = state.songs.filter { it.isUploadedMap.containsKey("jams") }
    val playerUnuploadedSongs = state.songs.filter { !it.isUploadedMap.containsKey("jams") }
    val allChartSongs = state.soundstreamSongs.sortedByDescending { it.weeklyStreams }
    val trendingSongs = allChartSongs.take(15)

    var selectedTab by remember { mutableStateOf("Music") }
    var currentTrackTitle by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.title ?: trendingSongs.firstOrNull()?.title ?: "Afro Beat Vibration") }
    var currentArtistName by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.let { state.artistName } ?: trendingSongs.firstOrNull()?.artistName ?: "Ademola") }
    var currentGenre by remember { mutableStateOf("Afro") }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = jamsDarkBg,
        bottomBar = {
            Column {
                SharedPlatformMiniPlayer(
                    trackTitle = currentTrackTitle,
                    artistName = currentArtistName,
                    genre = currentGenre,
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    accentColor = jamsColor
                )
                NavigationBar(
                    containerColor = Color(0xFF060B14),
                    contentColor = Color.White
                ) {
                    val tabs = listOf(
                        Triple("Music", Icons.Default.MusicNote, "music_tab"),
                        Triple("Trending", Icons.Default.Whatshot, "trending_tab"),
                        Triple("Library", Icons.Default.Folder, "library_tab"),
                        Triple("Account", Icons.Default.Person, "account_tab"),
                        Triple("Premium", Icons.Default.WorkspacePremium, "premium_tab")
                    )
                    tabs.forEach { (tab, icon, testTag) ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab,
                                    tint = if (selectedTab == tab) jamsColor else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) jamsColor else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            modifier = Modifier.testTag(testTag)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(jamsDarkBg)
        ) {
            when (selectedTab) {
                "Music" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("JAMS STREAM", color = jamsColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    Text("Discover African & Street Hits", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                PlayerAvatarDisplay(state = state, size = 40.dp)
                            }
                        }

                        // TRENDING NOW
                        item {
                            Text("Trending Now 🔥", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        items(trendingSongs) { song ->
                            val isPlayer = song.isPlayer || song.artistName == state.artistName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPlayer) Color(0xFF0F2E3A) else jamsCardBg,
                                border = if (isPlayer) BorderStroke(1.dp, jamsColor) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentTrackTitle = song.title
                                        currentArtistName = song.artistName
                                        currentGenre = song.genreName
                                        isPlaying = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProceduralCoverArt(
                                        title = song.title,
                                        genre = song.genreName,
                                        modifier = Modifier.size(42.dp),
                                        accentColor = jamsColor
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${song.artistName} • ${song.genreName}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${String.format("%,d", song.weeklyStreams)}", color = jamsColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("plays", color = Color.Gray, fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        // YOUR JAMS VAULT
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = jamsCardBg,
                                border = BorderStroke(1.dp, jamsColor.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Your JAMS Vault", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (playerUploadedSongs.isEmpty()) {
                                        Text("No songs uploaded to JAMS yet.", color = Color.Gray, fontSize = 12.sp)
                                    } else {
                                        playerUploadedSongs.forEach { s ->
                                            val streams = s.isUploadedMap["jams"] ?: 0L
                                            Text("• ${s.title} (${String.format("%,d", streams)} plays)", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                    if (playerUnuploadedSongs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.uploadSong(playerUnuploadedSongs.first().id, "jams") },
                                            colors = ButtonDefaults.buttonColors(containerColor = jamsColor),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Upload '${playerUnuploadedSongs.first().title}' ($700)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                "Trending" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Top 30 JAMS Chart", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        items(allChartSongs.take(30)) { song ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = jamsCardBg,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    currentTrackTitle = song.title
                                    currentArtistName = song.artistName
                                    isPlaying = true
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${song.currentRank}", color = jamsColor, fontSize = 14.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(28.dp))
                                    ProceduralCoverArt(title = song.title, genre = song.genreName, modifier = Modifier.size(38.dp), accentColor = jamsColor)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(song.artistName, color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Text("${String.format("%,d", song.weeklyStreams)}", color = Color.LightGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                "Library" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("Your JAMS Library", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (playerUploadedSongs.isEmpty()) {
                            Text("No tracks distributed to JAMS.", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(playerUploadedSongs) { song ->
                                    Surface(shape = RoundedCornerShape(8.dp), color = jamsCardBg, modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            ProceduralCoverArt(title = song.title, genre = "Afro", modifier = Modifier.size(40.dp), accentColor = jamsColor)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("${String.format("%,d", song.isUploadedMap["jams"] ?: 0L)} plays", color = jamsColor, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "jams", "JAMS", jamsColor, jamsDarkBg, jamsCardBg, 80000.0)
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "jams", "JAMS", jamsColor, jamsDarkBg, jamsCardBg, 80000.0)
                }

                "Account" -> {
                    val totalPlays = playerUploadedSongs.sumOf { it.isUploadedMap["jams"] ?: 0L }
                    val isPremium = state.upgradedPlatforms.contains("jams")
                    val rate = if (isPremium) 1.87 else 0.3
                    val estPayout = totalPlays * rate
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Creator Dashboard", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = jamsCardBg, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Total Plays on JAMS: ${String.format("%,d", totalPlays)}", color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Estimated Earnings: $${String.format("%,.2f", estPayout)}", color = jamsColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Payout rate: $$rate / play", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. SHARP (Inspired by SoundCloud - Underground Creator, Waveforms, Orange)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharpScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenOtherPlatform: (String) -> Unit = {}
) {
    val sharpColor = Color(0xFFFF5500)
    val sharpDarkBg = Color(0xFF0A0A0C)
    val sharpCardBg = Color(0xFF141418)

    val playerUploadedSongs = state.songs.filter { it.isUploadedMap.containsKey("sharp") }
    val playerUnuploadedSongs = state.songs.filter { !it.isUploadedMap.containsKey("sharp") }
    val allChartSongs = state.soundstreamSongs.sortedByDescending { it.weeklyStreams }

    val feedSongs = (playerUploadedSongs.map {
        ChartSong(
            id = it.id,
            title = it.title,
            artistName = state.artistName,
            artistId = "player",
            genreName = GameData.GENRES.find { g -> g.id == it.genreId }?.name ?: "Pop",
            rating = it.rating,
            weeklyStreams = it.isUploadedMap["sharp"] ?: 0L,
            totalStreams = it.totalStreams,
            weeksOnChart = 1,
            peakRank = 1,
            isPlayer = true,
            releasedWeek = it.weekCreated,
            releasedYear = it.yearCreated
        )
    } + allChartSongs).distinctBy { it.title + it.artistName }.take(25)

    var selectedTab by remember { mutableStateOf("Feed") }
    var likedSongIds by remember { mutableStateOf(setOf<String>()) }
    var repostedSongIds by remember { mutableStateOf(setOf<String>()) }
    var currentTrackTitle by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.title ?: feedSongs.firstOrNull()?.title ?: "Cyber Underground") }
    var currentArtistName by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.let { state.artistName } ?: feedSongs.firstOrNull()?.artistName ?: "DJ Pulse") }
    var currentGenre by remember { mutableStateOf("Electronic") }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = sharpDarkBg,
        bottomBar = {
            Column {
                SharedPlatformMiniPlayer(
                    trackTitle = currentTrackTitle,
                    artistName = currentArtistName,
                    genre = currentGenre,
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    accentColor = sharpColor
                )
                NavigationBar(
                    containerColor = Color(0xFF070709),
                    contentColor = Color.White
                ) {
                    val tabs = listOf(
                        Triple("Feed", Icons.Default.Stream, "feed_tab"),
                        Triple("Search", Icons.Default.Search, "search_tab"),
                        Triple("Library", Icons.Default.Favorite, "library_tab"),
                        Triple("Profile", Icons.Default.Person, "profile_tab"),
                        Triple("Premium", Icons.Default.WorkspacePremium, "premium_tab")
                    )
                    tabs.forEach { (tab, icon, testTag) ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab,
                                    tint = if (selectedTab == tab) sharpColor else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) sharpColor else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            modifier = Modifier.testTag(testTag)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(sharpDarkBg)
        ) {
            when (selectedTab) {
                "Feed" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("SHARP FEED", color = sharpColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    Text("The Underground Stream", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                if (playerUnuploadedSongs.isNotEmpty()) {
                                    Button(
                                        onClick = { viewModel.uploadSong(playerUnuploadedSongs.first().id, "sharp") },
                                        colors = ButtonDefaults.buttonColors(containerColor = sharpColor),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Upload ($500)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        items(feedSongs) { song ->
                            val isLiked = likedSongIds.contains(song.id)
                            val isReposted = repostedSongIds.contains(song.id)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = sharpCardBg,
                                border = if (song.isPlayer) BorderStroke(1.dp, sharpColor) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ProceduralCoverArt(
                                            title = song.title,
                                            genre = song.genreName,
                                            modifier = Modifier.size(46.dp),
                                            accentColor = sharpColor
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("${song.artistName} • Week ${song.releasedWeek}", color = Color.Gray, fontSize = 11.sp)
                                        }
                                        IconButton(
                                            onClick = {
                                                currentTrackTitle = song.title
                                                currentArtistName = song.artistName
                                                currentGenre = song.genreName
                                                isPlaying = true
                                            },
                                            modifier = Modifier.size(36.dp).background(sharpColor, CircleShape)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    // Animated Waveform Visualizer
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(24.dp)
                                            .background(Color(0xFF0F0F12), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val bars = 32
                                        for (i in 0 until bars) {
                                            val h = (8 + (i * 7 + (song.title.hashCode())) % 14).dp
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(h)
                                                    .background(if (i < 12) sharpColor else Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    likedSongIds = if (isLiked) likedSongIds - song.id else likedSongIds + song.id
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Like",
                                                    tint = if (isLiked) sharpColor else Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${(song.weeklyStreams / 25).coerceAtLeast(12) + if (isLiked) 1 else 0}", color = Color.Gray, fontSize = 11.sp)
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    repostedSongIds = if (isReposted) repostedSongIds - song.id else repostedSongIds + song.id
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Repeat,
                                                    contentDescription = "Repost",
                                                    tint = if (isReposted) sharpColor else Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${(song.weeklyStreams / 80).coerceAtLeast(3) + if (isReposted) 1 else 0}", color = Color.Gray, fontSize = 11.sp)
                                            }
                                        }
                                        Text("${String.format("%,d", song.weeklyStreams)} plays", color = sharpColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "Search" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("Search Sharp", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(feedSongs) { song ->
                                Surface(shape = RoundedCornerShape(8.dp), color = sharpCardBg, modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        ProceduralCoverArt(title = song.title, genre = song.genreName, modifier = Modifier.size(40.dp), accentColor = sharpColor)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(song.artistName, color = Color.Gray, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = {
                                            currentTrackTitle = song.title
                                            currentArtistName = song.artistName
                                            isPlaying = true
                                        }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = sharpColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Library" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("Your Sharp Library", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (playerUploadedSongs.isEmpty()) {
                            Text("No tracks uploaded to Sharp.", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(playerUploadedSongs) { song ->
                                    Surface(shape = RoundedCornerShape(8.dp), color = sharpCardBg, modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            ProceduralCoverArt(title = song.title, genre = "Electronic", modifier = Modifier.size(40.dp), accentColor = sharpColor)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("${String.format("%,d", song.isUploadedMap["sharp"] ?: 0L)} plays", color = sharpColor, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "sharp", "SHARP", sharpColor, sharpDarkBg, sharpCardBg, 70000.0)
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "sharp", "SHARP", sharpColor, sharpDarkBg, sharpCardBg, 70000.0)
                }

                "Profile" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))
                        PlayerAvatarDisplay(state = state, size = 64.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(state.artistName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("PRO UNLIMITED CREATOR", color = sharpColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = sharpCardBg, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Uploaded Masters: ${playerUploadedSongs.size}", color = Color.White, fontSize = 13.sp)
                                Text("Total Sharp Plays: ${String.format("%,d", playerUploadedSongs.sumOf { it.isUploadedMap["sharp"] ?: 0L })}", color = sharpColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. TUNES (Inspired by DatPiff / LiveMixtapes - Gold & Charcoal Street Hub)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunesScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenOtherPlatform: (String) -> Unit = {}
) {
    val tunesColor = Color(0xFFFFAA00)
    val tunesDarkBg = Color(0xFF111113)
    val tunesCardBg = Color(0xFF1A1A1E)

    val playerUploadedSongs = state.songs.filter { it.isUploadedMap.containsKey("tunes") }
    val playerUnuploadedSongs = state.songs.filter { !it.isUploadedMap.containsKey("tunes") }
    val allChartSongs = state.soundstreamSongs.sortedByDescending { it.weeklyStreams }
    val hotMixtapes = allChartSongs.take(15)

    var selectedTab by remember { mutableStateOf("Home") }
    var currentTrackTitle by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.title ?: hotMixtapes.firstOrNull()?.title ?: "Street Heat Vol. 1") }
    var currentArtistName by remember { mutableStateOf(playerUploadedSongs.firstOrNull()?.let { state.artistName } ?: hotMixtapes.firstOrNull()?.artistName ?: "Cyrus King") }
    var currentGenre by remember { mutableStateOf("Hip Hop") }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = tunesDarkBg,
        bottomBar = {
            Column {
                SharedPlatformMiniPlayer(
                    trackTitle = currentTrackTitle,
                    artistName = currentArtistName,
                    genre = currentGenre,
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    accentColor = tunesColor
                )
                NavigationBar(
                    containerColor = Color(0xFF09090B),
                    contentColor = Color.White
                ) {
                    val tabs = listOf(
                        Triple("Home", Icons.Default.Home, "home_tab"),
                        Triple("Trending", Icons.Default.Whatshot, "trending_tab"),
                        Triple("My Music", Icons.Default.Folder, "mymusic_tab"),
                        Triple("Premium", Icons.Default.WorkspacePremium, "premium_tab")
                    )
                    tabs.forEach { (tab, icon, testTag) ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab,
                                    tint = if (selectedTab == tab) tunesColor else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) tunesColor else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            modifier = Modifier.testTag(testTag)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(tunesDarkBg)
        ) {
            when (selectedTab) {
                "Home" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("TUNES MIXTAPES", color = tunesColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    Text("The Official Street Vault", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                PlayerAvatarDisplay(state = state, size = 40.dp)
                            }
                        }

                        // FREE UPLOAD ACTION
                        if (playerUnuploadedSongs.isNotEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF261D08),
                                    border = BorderStroke(1.dp, tunesColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Drop a Free Mixtape Single", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("100% Free distribution on Tunes", color = tunesColor, fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.uploadSong(playerUnuploadedSongs.first().id, "tunes") },
                                            colors = ButtonDefaults.buttonColors(containerColor = tunesColor),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Drop Track", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("Hot This Week 🔥", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        items(hotMixtapes) { song ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tunesCardBg,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    currentTrackTitle = song.title
                                    currentArtistName = song.artistName
                                    currentGenre = song.genreName
                                    isPlaying = true
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProceduralCoverArt(
                                        title = song.title,
                                        genre = song.genreName,
                                        modifier = Modifier.size(42.dp),
                                        accentColor = tunesColor
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${song.artistName} • ${song.genreName}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${String.format("%,d", song.weeklyStreams)}", color = tunesColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("downloads", color = Color.Gray, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                "Trending" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Top 30 Street Mixtape Chart", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        items(allChartSongs.take(30)) { song ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tunesCardBg,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    currentTrackTitle = song.title
                                    currentArtistName = song.artistName
                                    isPlaying = true
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${song.currentRank}", color = tunesColor, fontSize = 14.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(28.dp))
                                    ProceduralCoverArt(title = song.title, genre = song.genreName, modifier = Modifier.size(38.dp), accentColor = tunesColor)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(song.artistName, color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Text("${String.format("%,d", song.weeklyStreams)}", color = Color.LightGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                "My Music" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("My Tunes Drops", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (playerUploadedSongs.isEmpty()) {
                            Text("No tracks released on Tunes yet.", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(playerUploadedSongs) { song ->
                                    Surface(shape = RoundedCornerShape(8.dp), color = tunesCardBg, modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            ProceduralCoverArt(title = song.title, genre = "Hip Hop", modifier = Modifier.size(40.dp), accentColor = tunesColor)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("${String.format("%,d", song.isUploadedMap["tunes"] ?: 0L)} downloads", color = tunesColor, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Premium" -> {
                    PremiumUpgradeTab(state, viewModel, "tunes", "TUNES", tunesColor, tunesDarkBg, tunesCardBg, 50000.0)
                }

                "Search" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("Search Mixtapes", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(hotMixtapes) { song ->
                                Surface(shape = RoundedCornerShape(8.dp), color = tunesCardBg, modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        ProceduralCoverArt(title = song.title, genre = song.genreName, modifier = Modifier.size(40.dp), accentColor = tunesColor)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(song.artistName, color = Color.Gray, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = {
                                            currentTrackTitle = song.title
                                            currentArtistName = song.artistName
                                            isPlaying = true
                                        }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = tunesColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradePlatformCard(
    state: GameStateEntity,
    viewModel: GameViewModel,
    platformId: String,
    platformName: String,
    cost: Double,
    perks: String,
    accentColor: Color
) {
    if (!state.upgradedPlatforms.contains(platformId)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
            border = BorderStroke(1.dp, accentColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upgrade to $platformName Premium", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(perks, color = Color.LightGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.upgradePlatform(platformId, cost) },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upgrade for $${String.format("%,.0f", cost)}", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun PremiumUpgradeTab(
    state: GameStateEntity,
    viewModel: GameViewModel,
    platformId: String,
    platformName: String,
    themeColor: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color,
    cardBg: androidx.compose.ui.graphics.Color,
    upgradeCost: Double
) {
    val isPremium = state.upgradedPlatforms.contains(platformId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = themeColor,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$platformName Premium",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = if (isPremium) "Active Subscription • Artist Tier" else "Unlock Premium Artist Features",
            color = themeColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = cardBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Features Included:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                listOf(
                    "✓ Ad-free continuous music playback",
                    "✓ Offline listening downloads",
                    "✓ Ultra High Definition 320kbps Streaming",
                    "✓ Real-time Artist Analytics & Chart Tracking",
                    "✓ Instant playlist submission to Curators"
                ).forEach { perk ->
                    Text(text = perk, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
                if (isPremium) {
                    Text(
                        text = "You are currently a Premium Member.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = { viewModel.upgradePlatform(platformId, upgradeCost) },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Upgrade for $${java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(upgradeCost)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
